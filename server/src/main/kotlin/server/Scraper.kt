package server

import commons.Game
import commons.GameDsvInfo
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

class Scraper(val websiteUrl: String) {

    data class LeagueSetHolder(val set: MutableSet<League> = mutableSetOf())
    data class GameSetHolder(val set: MutableSet<Game> = mutableSetOf())

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
}