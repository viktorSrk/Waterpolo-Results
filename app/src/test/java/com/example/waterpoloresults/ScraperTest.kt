package com.example.waterpoloresults

import com.example.waterpoloresults.commons.League
import org.junit.Test

class ScraperTest {

    @Test
    fun quickTest() {
        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeLeagues()
    }

    @Test
    fun quickTestGames() {
        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
        val league = League(
            leagueId = 30,
            group = "",
            leagueKind = "L"
        )
        scraper.scrapeGames(league, 2023)
    }
}