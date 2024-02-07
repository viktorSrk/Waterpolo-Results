package server

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class ScraperTest {

    @Test
    fun scrape() {
        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
        val leagues = scraper.scrapeLeagues()
    }
}