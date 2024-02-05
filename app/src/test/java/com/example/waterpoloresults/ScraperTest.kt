package com.example.waterpoloresults

import org.junit.Test

class ScraperTest {

    @Test
    fun quickTest() {
        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeLeagues()
    }
}