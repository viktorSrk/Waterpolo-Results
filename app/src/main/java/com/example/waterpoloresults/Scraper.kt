package com.example.waterpoloresults

import com.example.waterpoloresults.commons.League
import com.example.waterpoloresults.commons.LeaguesScraped
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*
import java.net.URI
import java.net.URL

class Scraper(private var websiteUrl: String) {

    fun scrapeLeagues(): LeaguesScraped {
        val leagues = skrape(HttpFetcher) {
            request {
                url = websiteUrl + "Index.aspx"
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
                            leagueName,
                            parameters.get("LeagueID")!!.toInt(),
                            parameters.get("LeagueKind")!!,
                            leagueRegion))
                    }
                }
            }
        }

        return leagues
    }
}