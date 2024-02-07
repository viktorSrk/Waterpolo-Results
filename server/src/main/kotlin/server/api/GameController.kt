package server.api

import commons.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameRepository
import server.database.LeagueRepository

@RestController
@RequestMapping("/api/games")
class GameController(
    private val repo: GameRepository,
    @Autowired private val leagueRepo: LeagueRepository
) {

    @GetMapping(path = ["", "/"])
    fun getGames(): List<Game> = repo.findAll()

    @PostMapping(path = ["/add/{leagueId}"])
    fun addGame(@RequestBody game: Game, @PathVariable leagueId: Long): ResponseEntity<Game> {
        val assoc = leagueRepo.getReferenceById(leagueId)
        var saved: Game = repo.save(game)
        saved.league = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }
}