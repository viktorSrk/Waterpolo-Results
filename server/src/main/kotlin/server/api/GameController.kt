package server.api

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.LeagueDsvInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameDsvInfoRepository
import server.database.GameRepository
import server.database.LeagueRepository

@RestController
@RequestMapping("/api/games")
class GameController(
    private val repo: GameRepository,
    @Autowired private val leagueRepo: LeagueRepository,
    @Autowired private val dsvRepo: GameDsvInfoRepository
) {

    @GetMapping(path = ["", "/"])
    fun getGames(): List<Game> = repo.findAll()

    @GetMapping(path = ["/{id}"])
    fun getGameById(@PathVariable id: Long): Game = repo.getReferenceById(id)

    @PostMapping(path = ["/add/{leagueId}"])
    fun addGame(@RequestBody game: Game, @PathVariable leagueId: Long): ResponseEntity<Game> {
        val assoc = leagueRepo.getReferenceById(leagueId)
        var saved: Game = repo.save(game)
        saved.league = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }

//    @PutMapping(path = ["/updateResult/{id}"])
//    fun updateGameResult(@RequestBody result: GameResult, @PathVariable id: Long): ResponseEntity<Game> {
//        var assoc: Game
//        try {
//            assoc = repo.getReferenceById(id)
//        } catch (e: JpaObjectRetrievalFailureException) {
//            println("Game not found")
//            return ResponseEntity.badRequest().build()
//        }
//        assoc.result = result
//        assoc = repo.save(assoc)
//        return ResponseEntity.ok(assoc)
//    }

    fun getIdByDsvInfo(@RequestBody gameDsvInfo: GameDsvInfo, @RequestBody leagueDsvInfo: LeagueDsvInfo): Long {
        val game = repo.findGameByDsvInfo(gameDsvInfo.dsvGameId, leagueDsvInfo.dsvLeagueSeason, leagueDsvInfo.dsvLeagueId, leagueDsvInfo.dsvLeagueGroup, leagueDsvInfo.dsvLeagueKind)
        return game?.id ?: -1
    }

    @Transactional
    @PostMapping(path = ["/addAll/{leagueId}"])
    fun addAllGames(@RequestBody games: List<Game>, @PathVariable leagueId: Long): ResponseEntity<List<Game>> {
        if (games.isEmpty()) return ResponseEntity.ok(games)

        val assoc = leagueRepo.getReferenceById(leagueId)

        val updatedGames = mutableListOf<Game>()

        games.forEach { newGame ->
            val existingGame = repo.findGameByDsvInfo(
                newGame.dsvInfo?.dsvGameId ?: -1,
                assoc.dsvInfo?.dsvLeagueSeason ?: -1,
                assoc.dsvInfo?.dsvLeagueId ?: -1,
                assoc.dsvInfo?.dsvLeagueGroup ?: "",
                assoc.dsvInfo?.dsvLeagueKind ?: ""
            )

            if (existingGame != null) {
                // Update existing game fields here
                // Example: existingGame.someField = newGame.someField
                existingGame.home = newGame.home
                existingGame.away = newGame.away
                existingGame.date = newGame.date
//                existingGame.result = newGame.result
                updatedGames.add(repo.save(existingGame))
            } else {
                // Set the league association for the new game
                newGame.league = assoc
                updatedGames.add(repo.save(newGame))
            }
        }

//        val existingGames = assoc.games
//        repo.deleteAll(existingGames)
//
//        var saved: List<Game> = games.map { it.league = assoc; it }
//        saved = repo.saveAll(saved)

        return ResponseEntity.ok(updatedGames)
    }

    @PostMapping(path = ["/addDsvInfo/{gameId}"])
    fun setDsvInfo(@RequestBody dsvInfo: GameDsvInfo, @PathVariable gameId: Long): ResponseEntity<GameDsvInfo> {
        val assoc = repo.getReferenceById(gameId)
        var saved: GameDsvInfo = dsvRepo.save(dsvInfo)
        saved.game = assoc
        saved = dsvRepo.save(saved)
        return ResponseEntity.ok(saved)
    }
}