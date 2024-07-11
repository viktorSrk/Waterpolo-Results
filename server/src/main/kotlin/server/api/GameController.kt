package server.api

import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
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

    @PutMapping(path = ["/updateResult/{id}"])
    fun updateGameResult(@RequestBody result: GameResult, @PathVariable id: Long): ResponseEntity<Game> {
        var assoc = repo.getReferenceById(id)
        assoc.result = result
        assoc = repo.save(assoc)
        return ResponseEntity.ok(assoc)
    }

    @Transactional
    @PostMapping(path = ["/addAll/{leagueId}"])
    fun addAllGames(@RequestBody games: List<Game>, @PathVariable leagueId: Long): ResponseEntity<List<Game>> {
        if (games.isEmpty()) return ResponseEntity.ok(games)

        val assoc = leagueRepo.getReferenceById(leagueId)

        val existingGames = assoc.games
        repo.deleteAll(existingGames)

        var saved: List<Game> = games.map { it.league = assoc; it }
        saved = repo.saveAll(saved)

        return ResponseEntity.ok(saved)
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