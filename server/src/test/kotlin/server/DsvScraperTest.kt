package server

import commons.Game
import commons.GameDsvInfo
import commons.League
import commons.LeagueDsvInfo
import org.junit.jupiter.api.Test

class DsvScraperTest {

    @Test
    fun scrape() {
//        val scraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
//        scraper.scrapeLeagues()
    }

    @Test
    fun scrapeGames() {
        val scraper = DsvScraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeGames(
            League(
            dsvInfo = LeagueDsvInfo(
                dsvLeagueSeason = 2023,
                dsvLeagueId = 30,
                dsvLeagueGroup = "",
                dsvLeagueKind = "L"
            )
            )
        )
    }

    @Test
    fun scrapeGameResult() {
        val scraper = DsvScraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeGameResult(
            Game(
                dsvInfo = GameDsvInfo(
                    dsvGameId = 2,
                    game = Game(
                        league = League(
                            dsvInfo = LeagueDsvInfo(
                                dsvLeagueSeason = 2023,
                                dsvLeagueId = 30,
                                dsvLeagueGroup = "",
                                dsvLeagueKind = "L"
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun scrapeGameResult2() {
        val scraper = DsvScraper("https://dsvdaten.dsv.de/Modules/WB/")
        scraper.scrapeGameResult(
            Game(
                dsvInfo = GameDsvInfo(
                    dsvGameId = 5,
                    game = Game(
                        league = League(
                            dsvInfo = LeagueDsvInfo(
                                dsvLeagueSeason = 2023,
                                dsvLeagueId = 30,
                                dsvLeagueGroup = "",
                                dsvLeagueKind = "L"
                            )
                        )
                    )
                )
            )
        )
    }
}