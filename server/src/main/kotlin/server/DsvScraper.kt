package server

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import commons.TeamSheet
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent
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

class DsvScraper(val websiteUrl: String) {

    data class LeagueSetHolder(val set: MutableSet<League> = mutableSetOf())
    data class GameSetHolder(val set: MutableSet<Game> = mutableSetOf())
    data class GameEventListHolder(
        val goals: MutableList<GoalGameEvent> = mutableListOf(),
        val exclusions: MutableList<ExclusionGameEvent> = mutableListOf(),
        val penalties: MutableList<PenaltyGameEvent> = mutableListOf(),
        val timeouts: MutableList<TimeoutGameEvent> = mutableListOf(),
        val others: MutableList<GameEvent> = mutableListOf()
    )
    data class TeamSheetHolder(val teamSheets: MutableList<TeamSheet> = mutableListOf())

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

    /**
     * dsvLeagueIds Map<leagueId, Map<leagueKind, List<LeagueGroup>>>
     */
    fun scrapeCertainLeagues(
        dsvLeagueIds: Map<Int, Map<String, List<String>>>,
        leagueNames: Map<Int, String>,
        leagueRegions: Map<Int, String>
    ): List<League> {
        val leagues = LeagueSetHolder()

        for (id in dsvLeagueIds.keys) {
            for ((kind, groups) in dsvLeagueIds[id]!!) { for (group in groups) {
                val dsvInfo = LeagueDsvInfo(
                    dsvLeagueSeason = 2024,
                    dsvLeagueId = id,
                    dsvLeagueGroup = group,
                    dsvLeagueKind = kind
                )

                val name = leagueNames[id]
                    ?: skrape(HttpFetcher) {
                        request {
                            url = websiteUrl + dsvInfo.buildLeagueLink()
                        }
                        extractIt {
                            htmlDocument {
                                findFirst("#ContentSection__headerLabel").text
                            }
                        }
                    }
                val region = leagueRegions[id] ?: "Unknown"
                val country = "DEU"

                leagues.set.add(League(
                    name = name,
                    country = country,
                    region = region,
                    dsvInfo = dsvInfo
                ))
            }}
        }

        return leagues.set.toList()
    }

    fun scrapeGames(league: League): List<Game> {
        if (league.dsvInfo == null) return emptyList()

        println("Scraping games for league ${league.name}")

        val games = skrape(HttpFetcher) {
            request {
                url = websiteUrl + league.dsvInfo!!.buildLeagueLink()
                println(url)
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

                    if (gameRows.isEmpty()) {
                        println("No games found")
                        return@htmlDocument
                    }

                    for (row in gameRows) {
                        val home = row.children[2].text
                        val away = row.children[3].text

                        var date: Long
                        var dateHolderText = row.children[1].text
                        try {
                            if (dateHolderText.startsWith("unbekannt")) {
                                date = Long.MAX_VALUE
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
                            date = Long.MAX_VALUE
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

        println("Scraping result for game ${game.home} vs ${game.away}")

        val gameResult = skrape(HttpFetcher) {
            request {
                url = websiteUrl + game.dsvInfo!!.buildGameLink()
                println(url)
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
                        val quarterScores = findFirst("#ContentSection_scoreboard_data").findFirst(".table").findAll("tr")[2].findAll("td")
                        val quarterScoresHome = quarterScores[0].text.split("-")
                        val quarterScoresAway = quarterScores[1].text.split("-")
                        for (i in 0..3) {
                            homeScore[i] = quarterScoresHome[i].toInt()
                            awayScore[i] = quarterScoresAway[i].toInt()
                        }
                    } catch (_: ElementNotFoundException) {
                        println("Could not find quarter scores")
                    } catch (_: IndexOutOfBoundsException) {
                        val totalScores = findFirst("#ContentSection_scoreboard_data").findFirst(".table").findAll("tr")[1].findAll("td")
                        homeScore[0] = totalScores[0].text.toInt()
                        awayScore[0] = totalScores[1].text.toInt()
                    }

                    if ((homeScore.sum() == 10 && awayScore.sum() == 0) || (homeScore.sum() == 0 && awayScore.sum() == 10)) {
                        println("Game won through decision not play!")
                        finished = true
                    }

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
                            if (eventQuarterString == "5m" || eventQuarterString == "EW") {
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
                                "A" -> {
                                    result.exclusions.add(ExclusionGameEvent(
                                        time = eventTime,
                                        quarter = eventQuarter,
                                        excludedName = eventPlayerName,
                                        excludedNumber = (if (eventFromHomeTeam) eventHomeLabel else eventAwayLabel).toIntOrNull() ?: 0,
                                        excludedTeamHome = eventFromHomeTeam
                                    ))
                                }
                                "S" -> {
                                    result.penalties.add(PenaltyGameEvent(
                                        time = eventTime,
                                        quarter = eventQuarter,
                                        penalizedName = eventPlayerName,
                                        penalizedNumber = (if (eventFromHomeTeam) eventHomeLabel else eventAwayLabel).toIntOrNull() ?: 0,
                                        penalizedTeamHome = eventFromHomeTeam
                                    ))
                                }
                                "AU" -> {
                                    result.timeouts.add(TimeoutGameEvent(
                                        time = eventTime,
                                        quarter = eventQuarter,
                                        teamHome = eventFromHomeTeam
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

        return gameEvents.goals +
                gameEvents.exclusions +
                gameEvents.penalties +
                gameEvents.timeouts +
                gameEvents.others
    }

    fun scrapeTeamSheets(result: GameResult): List<TeamSheet> {
        if (result.game == null || result.game!!.dsvInfo == null) return emptyList()

        val teamSheet = skrape(HttpFetcher) {
            request {
                url = websiteUrl + result.game!!.dsvInfo!!.buildGameLink()
            }

            extractIt<TeamSheetHolder> { result ->
                htmlDocument {
                    val statsContainer = findFirst("#stats").findAll("tbody")
                    statsContainer.forEach {
                        val rows = it.findAll("tr")

                        val players = mutableListOf<TeamSheet.Player>()
                        for (r in rows) {
                            val number = r.children[0].text.toIntOrNull() ?: continue
                            val name = r.children[1].text
                            players.add(TeamSheet.Player(number, name))
                        }

                        val coach: String = try {
                            it.findFirst("#ContentSection__whitecoachLabel").text.drop("Trainer: ".length)
                        } catch(e: ElementNotFoundException) {
                            try {
                                it.findFirst("#ContentSection__bluecoachLabel").text.drop("Trainer: ".length)
                            } catch(e: ElementNotFoundException) {
                                ""
                            }
                        }

                        result.teamSheets.add(TeamSheet(
                            players = players.toList(),
                            coach = coach
                        ))
                    }
                }
            }
        }

        return teamSheet.teamSheets
    }
}