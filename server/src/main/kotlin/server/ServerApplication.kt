package server

import commons.Game
import commons.League
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import server.api.GameController
import server.api.LeagueController

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplication

fun main(args: Array<String>) {
	val context: ApplicationContext = runApplication<ServerApplication>(*args)

	val dsvScraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")
	val leagueController: LeagueController = context.getBean(LeagueController::class.java)
	val gameController: GameController = context.getBean(GameController::class.java)

	while (true) {
		val leagues: List<League> = dsvScraper.scrapeLeagues()

		for (l in leagues) {
			val response: ResponseEntity<League> = leagueController.addLeague(l)
			val leagueId: Long = response.body!!.id
			leagueController.setDsvInfo(l.dsvInfo!!, leagueId)

			val games: List<Game> = dsvScraper.scrapeGames(leagueController.getLeagueById(leagueId))
			for (g in games) {
				gameController.addGame(g, leagueId)
			}
		}
	}
}

