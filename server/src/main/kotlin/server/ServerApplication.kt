package server

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplication

suspend fun main(args: Array<String>) {
	val context: ApplicationContext = runApplication<ServerApplication>(*args)

	val dsvScraper = DsvScraper("https://dsvdaten.dsv.de/Modules/WB/")
	val dsvLiveScraper = DsvLiveScraper("https://lizenz.dsv.de/")

	val leagueController: LeagueController = context.getBean(LeagueController::class.java)
	val gameController: GameController = context.getBean(GameController::class.java)
	val gameResultController: GameResultController = context.getBean(GameResultController::class.java)
	val gameEventController: GameEventController = context.getBean(GameEventController::class.java)
	val teamSheetController: TeamSheetController = context.getBean(TeamSheetController::class.java)

//	LeaguesToScrape.dsvLeagueIds = mapOf(
//		142 to mapOf(
//			"L" to listOf("")
//		)
//	) // TODO: remove
	while (true)
		try {
			println("scraping ...")
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
			break
		} catch (e: Exception) {
			println("Error while scraping: ${e.message}")
//			e.printStackTrace()
			println("Trying to scrape again ...")
		}

	var liveGames = dsvLiveScraper.getLiveGames()

	val jobs = mutableListOf<Job>()
	val scrapingJob = GlobalScope.launch {
		while (true) {
			try {
				println("scraping ...")
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
			} catch (e: Exception) {
				println("Error while scraping: ${e.message}")
//				e.printStackTrace()
				println("Trying to scrape again ...")
			}
		}
	}
	val liveScrapingJob = GlobalScope.launch {
		while (true)
			try {
				println("scraping live ...")
				liveGames = dsvLiveScraper.getLiveGames()
				val liveJobs = liveGames.map {
					GlobalScope.launch {
						println("scraping live game ...")
						val leagueDsvInfo = it.first.first
						val gameDsvInfo = it.first.second

						val gameId = gameController.getIdByDsvInfo(gameDsvInfo, leagueDsvInfo)

						if (gameId == -1L) {
							return@launch
						}

						val game = gameController.getGameById(gameId)
						scrapeCertainGameLive(dsvLiveScraper, game, liveGames, gameController, true, gameResultController, gameEventController)
					}
				}
				liveJobs.joinAll()
			} catch (e: Exception) {
				println("Error while scraping live: ${e.message}")
//				e.printStackTrace()
				println("Trying to scrape live again ...")
			}
	} // TODO: extract liveJobs from liveScrapingJob, in order that new liveGames are fetched while liveJobs is running

	jobs.add(scrapingJob)
	jobs.add(liveScrapingJob)
	jobs.joinAll()

//	while (true) {
//		try {
//			println("Scraping certain leagues ...")
//			scrapeCertainLeagues(
//				dsvScraper,
//				leagueController,
//				gameController,
//				gameResultController,
//				gameEventController,
//				teamSheetController,
//
////				LeaguesToScrape.dsvLeagueIds,
//				mapOf(
//					142 to mapOf(
//						"L" to listOf("")
//					)
//				),
//				LeaguesToScrape.leagueNames,
//				LeaguesToScrape.leagueRegions
//			)
//			// TODO: Remove this test code and implement the actual scraping logic through multiple threads
//			val liveGames = dsvLiveScraper.getLiveGames()
//			val liveJobs = liveGames.map {
//				GlobalScope.launch {
//					val leagueDsvInfo = it.first.first
//					val gameDsvInfo = it.first.second
//
//					val gameId = gameController.getIdByDsvInfo(gameDsvInfo, leagueDsvInfo)
//
//					if (gameId == -1L) {
//						return@launch
//					}
//
//					val game = gameController.getGameById(gameId)
//					scrapeCertainGameLive(dsvLiveScraper, game, liveGames, true, gameController)
//				}
//			}
//			liveJobs.joinAll()
//		} catch (e: Exception) {
//			println("Error: ${e.message}")
//			println("Trying again ...")
//		}
//	}
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


fun scrapeCertainGameLive(
	dsvLiveScraper: DsvLiveScraper,

	game: Game,
	liveGames: List<Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Array<Any>>>,
	gameController: GameController,

	pushToRepo: Boolean = false,
	gameResultController: GameResultController? = null,
	gameEventController: GameEventController? = null
): Game {
	var game = game

	if (pushToRepo && (gameResultController == null || gameEventController == null)) {
		throw IllegalArgumentException("gameController/gameEventController must be provided and be non-null when pushToRepo is true")
	}

	val liveGame = liveGames.find {
		(it.first.first.equals(game.league?.dsvInfo) ?: false)
				&& it.first.second.equals(game.dsvInfo)
	}

	if (liveGame == null) {
		throw NoSuchElementException("Cannot find the game within liveGames")
	}

	while (game.result?.finished != true) { // TODO: change condition to check if game is finished and if game has started (no new games are found while this is running)
		val events = dsvLiveScraper.getGameEvents(
			liveGame.first.first,
			liveGame.first.second,
			liveGame.second[0] as String,
			liveGame.second[1] as Int,
			liveGame.second[2] as String,
			liveGame.second[3] as Int
		)

		game.result = game.result ?: GameResult()
		game.result!!.gameEvents = events
		game.result!!.calculateTeamScoreFromEvents(teamHome = true)
		game.result!!.calculateTeamScoreFromEvents(teamAway = true)

		if (pushToRepo) {
			val gameResultResponse = gameResultController!!.updateResult(game.result!!, game.id)
			val gameResultId = gameResultResponse.body!!.id
			gameEventController!!.addAllEvents(events, gameResultId)
		}

		game = gameController.getGameById(game.id)
	}

	return game
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

