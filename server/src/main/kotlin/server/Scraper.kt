package server

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
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
    data class BooleanHolder(var value: Boolean = false)

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

                        val homeScore = arrayOf(0, 0, 0, 0)
                        val awayScore = arrayOf(0, 0, 0, 0)
                        var resultString = row.children[6].text
                        if (!resultString.isEmpty() && resultString.startsWith("(")) {
                            resultString = resultString.drop(1).dropLast(1)
                            val quarterResults = resultString.split(", ")
                            for (i in 0..3) {
                                val scores = quarterResults[i].split(":")
                                homeScore[i] = scores[0].toInt()
                                awayScore[i] = scores[1].toInt()
                            }
                        }

                        val dsvLink = row.findFirst("a").attribute("href").drop(websiteUrl.length)
                        if (!dsvLink.startsWith("Game.aspx")) continue
                        val dsvParams = HashMap<String, String>()
                        for (param in dsvLink.drop("Game.aspx?".length).split("&")) {
                            val split = param.split("=")
                            dsvParams.put(split[0], split[1])
                        }
                        val dsvId = dsvParams["GameID"]!!.toInt()

                        val finished = gameFinished(Game(
                            dsvInfo = GameDsvInfo(
                                game = Game(
                                    league = league
                                ),
                                dsvGameId = dsvId
                            )
                        ))

                        results.set.add(Game(
                            home = home,
                            away = away,
                            date = date,
                            result = GameResult(
                                homeScore = homeScore,
                                awayScore = awayScore,
                                finished = finished
                            ),
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

    private fun gameFinished(game: Game): Boolean {
        if (game.dsvInfo == null) return false

        val result = skrape(HttpFetcher) {
            request {
                url = websiteUrl + game.dsvInfo!!.buildGameLink()
            }

            extractIt<BooleanHolder> {holder ->
                var finished: Boolean = false
                htmlDocument {
                    val endTimeString = findFirst("#ContentSection__endgameLabel").text
                    if (endTimeString.isEmpty()) {
                        finished = false
                    } else {
                        finished = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).parse(
                            endTimeString.dropLastWhile { !it.isDigit() }
                        ).time < System.currentTimeMillis()
                    }
                }
                holder.value = finished
            }
        }

        return result.value
    }
}