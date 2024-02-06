package com.example.waterpoloresults

import com.example.waterpoloresults.commons.*
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.ElementNotFoundException
import it.skrape.selects.html5.*
import java.net.URI
import java.net.URL
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class Scraper(val websiteUrl: String) {

    fun scrapeLeagues(): LeaguesScraped {
        val leagues = skrape(HttpFetcher) {
            request {
                url = websiteUrl + "Index.aspx"
                timeout = 10000
            }

            extractIt<LeaguesScraped> {results ->
                htmlDocument{
                    val leagueEntry = table(".table.table-sm") { tbody{ tr { td { a { findAll{this} } } } } }

                    for (league in leagueEntry) {
                        val leagueName = league.text

                        val leagueLink = league.attribute("href")
                        if (!leagueLink.startsWith("League.aspx")) {
                            continue
                        }
                        val leagueUrl = URL(websiteUrl + "/" + leagueLink)
                        val parametersArr = URI(leagueUrl.protocol, leagueUrl.userInfo, leagueUrl.host,
                            leagueUrl.port, leagueUrl.path, leagueUrl.query, leagueUrl.ref)
                            .query.split("&")
                        val parameters = HashMap<String, String>()
                        for (parameter in parametersArr) {
                            val parts = parameter.split("=")
                            parameters.put(parts[0], parts[1])
                        }

                        val leagueRegion = "DEU - " + league.parent.parent.parent.parent.parent.siblings.first().text

                        results.leagues.add(League(
                            name = leagueName,
                            leagueId =  parameters.get("LeagueID")!!.toInt(),
                            group = parameters.get("Group")!!,
                            leagueKind =  parameters.get("LeagueKind")!!,
                            region =  leagueRegion))
                    }
                }
            }
        }

        return leagues
    }

    fun scrapeGames(league: League, season: Int): GamesScraped {
        val games = skrape(HttpFetcher) {
            request {
                url = websiteUrl + league.buildLeagueLink(season)
                timeout = 10000
            }

            extractIt<GamesScraped> {results ->
                htmlDocument {
                    val gameRows = findFirst("#games").findFirst(".table").findAll("tr").
                    filter { try {
                        it.findFirst("a")
                        true
                    } catch (e: ElementNotFoundException) {
                        false
                    }}

                    for (row in gameRows) {
                        val gameLink = row.findFirst("a").attribute("href")
                        val gameUrl = URL(gameLink)
                        val parametersArr = URI(gameUrl.protocol, gameUrl.userInfo, gameUrl.host,
                            gameUrl.port, gameUrl.path, gameUrl.query, gameUrl.ref)
                            .query.split("&")
                        val parameters = HashMap<String, String>()
                        for (parameter in parametersArr) {
                            val parts = parameter.split("=")
                            parameters.put(parts[0], parts[1])
                        }

                        val gameId = parameters.get("GameID")!!.toInt()
                        val leagueId = parameters.get("LeagueID")!!.toInt()

                        val dateHolder = row.children.get(1)
                        var date: Date
                        if (dateHolder.text == "unbekannt"
                            || dateHolder.text.split(" ")[0] == "unbekannt"
                            || dateHolder.text.split(" ")[1] == "unbekannt") {
                            date = Date(0)
                        } else {
                            val dateString = dateHolder.text.split(" ")[0]
                            val dateParts = dateString.split(".")
                            val year = dateParts[2].dropLast(1).toInt() + 2000
//                        val year = 2024
                            val month = dateParts[1].toInt() - 1
                            val day = dateParts[0].toInt()
                            val timeString = dateHolder.text.split(" ")[1]
                            val timeParts = timeString.split(":")
                            val hour = timeParts[0].toInt()
                            val minute = timeParts[1].toInt()
                            val calendar =
                                Calendar.getInstance(TimeZone.getTimeZone("Berlin/Germany"))
                            calendar.set(year, month, day, hour, minute)

                            date = calendar.time
                        }
                        val homeTeam = row.children.get(2).text
                        val guestTeam = row.children.get(3).text

//                        val gameResult: Result
//                        if (date.before(Date()) && row.children.get(6).text != "") {
//                            gameResult = Result(gameId = gameId, leagueId = leagueId)
//                            val resultString = row.children.get(6).text.drop(1).dropLast(1)
//                            val quarters = resultString.split(", ")
//                            for (i in 0..3) {
//                                val goals = quarters[i].split(":")
//                                gameResult.home.set(i, goals[0].toInt())
//                                gameResult.guest.set(i, goals[1].toInt())
//                            }
//                        } else {
//                            gameResult = Result()
//                        }

                        results.games.add(Game(
                            leagueId = leagueId,
                            leagueGroup = league.group,
                            leagueKind = league.leagueKind,
                            gameId = gameId,
                            season = season,
                            date = date.time,
                            homeTeam = homeTeam,
                            guestTeam = guestTeam))
                    }
                }
            }
        }

        return games
    }
}