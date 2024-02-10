package server

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.ElementNotFoundException
import it.skrape.selects.html5.a
import it.skrape.selects.html5.table
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class Scraper(val websiteUrl: String) {

    data class LeagueSetHolder(val set: MutableSet<League> = mutableSetOf())
    data class GameSetHolder(val set: MutableSet<Game> = mutableSetOf())
    data class GameEventListHolder(val goals: MutableList<GoalGameEvent> = mutableListOf(), val others: MutableList<GameEvent> = mutableListOf())

    fun scrapeLeagues(): List<League> {
        val leagues = skrape(HttpFetcher) {
            request {
                url = websiteUrl + "Index.aspx"
            }

            extractIt<LeagueSetHolder> {results ->
                htmlDocument {
                    val leagueEntries = table(".table.table-sm") { tbody { tr { td { a { findAll{this} } } } } }

                    for (league in leagueEntries) {
                        val name = league.text

                        val dsvLink = league.attribute("href")
                        if (!dsvLink.startsWith("League.aspx")) continue
                        val dsvParams = HashMap<String, String>()
                        for (param in dsvLink.drop("League.aspx?".length).split("&")) {
                            val split = param.split("=")
                            dsvParams.put(split[0], split[1])
                        }
                        val dsvSeason = dsvParams["Season"]!!.toInt()
                        val dsvId = dsvParams["LeagueID"]!!.toInt()
                        val dsvGroup = dsvParams["Group"]
                        val dsvKind = dsvParams["LeagueKind"]

                        val region = league.parent.parent.parent.parent.parent.siblings.first().text
                        if (region.startsWith("aktuelle Woche")) continue

                        results.set.add(League(
                            name = name,
                            country = "DEU",
                            region = region,
                            dsvInfo = LeagueDsvInfo(
                                dsvLeagueSeason = dsvSeason,
                                dsvLeagueId = dsvId,
                                dsvLeagueGroup = dsvGroup!!,
                                dsvLeagueKind = dsvKind!!
                            )
                        ))
                    }
                }
            }
        }

        return leagues.set.toList()
    }

    fun scrapeGames(league: League): List<Game> {
        if (league.dsvInfo == null) return emptyList()

        val games = skrape(HttpFetcher) {
            request {
                url = websiteUrl + league.dsvInfo!!.buildLeagueLink()
            }

            extractIt<GameSetHolder> { results ->
                htmlDocument {
                    val gameRows = findFirst("#games").findFirst(".table").findAll("tr").
                    filter { try {
                        it.findFirst("a")
                        true
                    } catch (e: ElementNotFoundException) {
                        false
                    }}

                    for (row in gameRows) {
                        val home = row.children[2].text
                        val away = row.children[3].text

                        var date: Long
                        var dateHolderText = row.children[1].text
                        try {
                            if (dateHolderText.startsWith("unbekannt")) {
                                date = -1
                            } else if (dateHolderText.endsWith("unbekannt")) {
                                dateHolderText = dateHolderText.dropLast("unbekannt".length)
                                date = SimpleDateFormat("dd.MM.yy, ", Locale.getDefault()).parse(
                                    dateHolderText
                                ).time
                            } else {
                                dateHolderText = dateHolderText.dropLast(" Uhr".length)
                                date =
                                    SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault()).parse(
                                        dateHolderText
                                    ).time
                            }
                        } catch (e: ParseException) {
                            date = -1
                        }

                        val dsvLink = row.findFirst("a").attribute("href").drop(websiteUrl.length)
                        if (!dsvLink.startsWith("Game.aspx")) continue
                        val dsvParams = HashMap<String, String>()
                        for (param in dsvLink.drop("Game.aspx?".length).split("&")) {
                            val split = param.split("=")
                            dsvParams.put(split[0], split[1])
                        }
                        val dsvId = dsvParams["GameID"]!!.toInt()

                        results.set.add(Game(
                            home = home,
                            away = away,
                            date = date,
                            dsvInfo = GameDsvInfo(
                                dsvGameId = dsvId
                            )
                        ))
                    }
                }
            }
        }

        return games.set.toList()
    }

    fun scrapeGameResult(game: Game): GameResult {
        if (game.dsvInfo == null) return GameResult()

        val gameResult = skrape(HttpFetcher) {
            request {
                url = websiteUrl + game.dsvInfo!!.buildGameLink()
            }

            extractIt<GameResult> { result ->
                htmlDocument {

                    var finished = false
                    val endDateString = findFirst("#ContentSection__endgameLabel").text
                    if (endDateString.isNotEmpty()) {
                        val endDate: Long = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            .parse(endDateString.dropLastWhile { !it.isDigit() }).time
                        finished = endDate < System.currentTimeMillis()
                    }

                    val homeScore = arrayOf(0, 0, 0, 0)
                    val awayScore = arrayOf(0, 0, 0, 0)
                    try {
                        for (i in 0..3) {
                            val quarterScoreHome =
                                findFirst("#ContentSection__${i + 1}homeLabel").text
                            val quarterScoreAway =
                                findFirst("#ContentSection__${i + 1}guestLabel").text
                            homeScore[i] = quarterScoreHome.toInt()
                            awayScore[i] = quarterScoreAway.toInt()
                        }
                    } catch (_: ElementNotFoundException) {}

                    result.finished = finished
                    result.homeScore = homeScore
                    result.awayScore = awayScore
                }
            }
        }

        return gameResult
    }

    fun scrapeGameEvents(result: GameResult): List<GameEvent> {
        if (result.game == null || result.game!!.dsvInfo == null) return emptyList()

        val gameEvents = skrape(HttpFetcher) {
            request {
                url = websiteUrl + result.game!!.dsvInfo!!.buildGameLink()
            }

            extractIt<GameEventListHolder> { result ->
                htmlDocument {

                    var currentEventIndex = 0

                    while (true) {
                        try {
                            var eventQuarterString = findFirst("#ContentSection__gameRepeater__periodLabel_${currentEventIndex}").text
                            if (eventQuarterString == "5m") {
                                eventQuarterString = "5"
                            }
                            val eventQuarter = eventQuarterString.toInt()

                            val eventTimeHolder = findFirst("#ContentSection__gameRepeater__timeLabel_${currentEventIndex}")
                            val eventTime = if (eventTimeHolder.text.isEmpty()) {
                                0L
                            } else {
                                val eventTimeParts = eventTimeHolder.text.split(":")
                                60 * eventTimeParts[0].toLong() + eventTimeParts[1].toLong()
                            }

                            val eventHomeLabel = findFirst("#ContentSection__gameRepeater__homeLabel_${currentEventIndex}").text
                            val eventAwayLabel = findFirst("#ContentSection__gameRepeater__guestLabel_${currentEventIndex}").text
                            val eventFromHomeTeam = eventHomeLabel.isNotEmpty() && eventAwayLabel.isEmpty()

                            val eventPlayerName = findFirst("#ContentSection__gameRepeater__playerLabel_${currentEventIndex}").text

                            val eventType = findFirst("#ContentSection__gameRepeater__eventkeyLabel_${currentEventIndex}").text

                            if (eventTimeHolder.attribute("style") == "text-decoration: line-through;") {
                                currentEventIndex++
                                continue
                            }

                            when (eventType) {
                                "T" -> {
                                    result.goals.add(GoalGameEvent(
                                        time = eventTime,
                                        quarter = eventQuarter,
                                        scorerName = eventPlayerName,
                                        scorerNumber = (if (eventFromHomeTeam) eventHomeLabel else eventAwayLabel).toInt(),
                                        scorerTeamHome = eventFromHomeTeam
                                    ))
                                }
                                else -> {
                                    result.others.add(GameEvent(
                                        time = eventTime,
                                        quarter = eventQuarter
                                    ))
                                }
                            }
                        } catch (e: ElementNotFoundException) {
                            break
                        } finally {
                            currentEventIndex++
                        }
                    }
                }
            }
        }

        return gameEvents.goals + gameEvents.others
    }
}