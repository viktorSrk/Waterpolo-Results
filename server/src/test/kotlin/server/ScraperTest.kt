package server

import org.junit.jupiter.api.Test

class ScraperTest {

    @Test
    fun scrape() {
        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeLeagues()
    }
}