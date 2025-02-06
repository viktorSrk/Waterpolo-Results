package server

import commons.Game
import commons.League
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import server.api.GameController
import server.api.GameEventController
import server.api.GameResultController
import server.api.LeagueController
import server.api.TeamSheetController
import java.lang.reflect.InvocationTargetException

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplication

fun main(args: Array<String>) {
	val context: ApplicationContext = runApplication<ServerApplication>(*args)

	val dsvScraper = DsvScraper("https://dsvdaten.dsv.de/Modules/WB/")
	val leagueController: LeagueController = context.getBean(LeagueController::class.java)
	val gameController: GameController = context.getBean(GameController::class.java)
	val gameResultController: GameResultController = context.getBean(GameResultController::class.java)
	val gameEventController: GameEventController = context.getBean(GameEventController::class.java)
	val teamSheetController: TeamSheetController = context.getBean(TeamSheetController::class.java)

	while (true) {
//		scrapeAllDsv(
//			dsvScraper,
//			leagueController,
//			gameController,
//			gameResultController,
//			gameEventController,
//			teamSheetController
//		)
		scrapeCertainLeagues(
			dsvScraper,
			leagueController,
			gameController,
			gameResultController,
			gameEventController,
			teamSheetController,

			LeaguesToScrape.dsvLeagueIds,
			LeaguesToScrape.leagueNames,
			LeaguesToScrape.leagueRegions
		)
	}
}

fun scrapeCertainLeagues(
	dsvScraper: DsvScraper,
	leagueController: LeagueController,
	gameController: GameController,
	gameResultController: GameResultController,
	gameEventController: GameEventController,
	teamSheetController: TeamSheetController,

	dsvLeagueIds: Map<Int, Map<String, List<String>>>,
	leagueNames: Map<Int, String>,
	leagueRegions: Map<Int, String>
) {
	val leagues: List<League> = dsvScraper.scrapeCertainLeagues(dsvLeagueIds, leagueNames, leagueRegions)

	for (l in leagues) {
		var success = false
		while (!success) {
			try {
				val leagueResponse: ResponseEntity<League> = leagueController.addLeague(l)

				val leagueId: Long = leagueResponse.body!!.id
				if (leagueResponse.body!!.dsvInfo != l.dsvInfo) {
					leagueController.setDsvInfo(l.dsvInfo!!, leagueId)
				}

				val games: List<Game> =
					dsvScraper.scrapeGames(leagueController.getLeagueById(leagueId))
				val gamesResponse = gameController.addAllGames(games, leagueId)

				for (g in gamesResponse.body!!) {
					val gameId = g.id

					gameController.setDsvInfo(g.dsvInfo!!, gameId)

					val addedGame = gameController.getGameById(gameId)
					var result = dsvScraper.scrapeGameResult(addedGame)
					val resultResponse = gameResultController.setResult(result, gameId)
					result = resultResponse.body!!

					val gameEvents = dsvScraper.scrapeGameEvents(result)
					gameEventController.addAllEvents(gameEvents, result.id)

					val teamSheets = dsvScraper.scrapeTeamSheets(result)
					teamSheetController.addTeamSheets(teamSheets, result.id)
				}

				success = true
			} catch (e: Exception) {
				println("Exception: ${e.message}")
				println("Retrying...")
			}
		}
	}
}


fun scrapeAllDsv(
	dsvScraper: DsvScraper,
	leagueController: LeagueController,
	gameController: GameController,
	gameResultController: GameResultController,
	gameEventController: GameEventController,
	teamSheetController: TeamSheetController
) {
	val leagues: List<League> = dsvScraper.scrapeLeagues()

	for (l in leagues) {
		val leagueResponse: ResponseEntity<League> = leagueController.addLeague(l)
		val leagueId: Long = leagueResponse.body!!.id
		leagueController.setDsvInfo(l.dsvInfo!!, leagueId)

		val games: List<Game> = dsvScraper.scrapeGames(leagueController.getLeagueById(leagueId))
		for (g in games) {
			val gameResponse = gameController.addGame(g, leagueId)
			val gameId: Long = gameResponse.body!!.id

			gameController.setDsvInfo(g.dsvInfo!!, gameId)

			val addedGame = gameController.getGameById(gameId)
			var result = dsvScraper.scrapeGameResult(addedGame)
			val resultResponse = gameResultController.setResult(result, gameId)
			result = resultResponse.body!!

			val gameEvents = dsvScraper.scrapeGameEvents(result)
			gameEventController.addAllEvents(gameEvents, result.id)

			val teamSheets = dsvScraper.scrapeTeamSheets(result)
			teamSheetController.addTeamSheets(teamSheets, result.id)
		}
	}
}

