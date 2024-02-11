package server.api

import commons.GameResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameRepository
import server.database.GameResultRepository

@RestController
@RequestMapping("/api/games/result")
class GameResultController(
    private val repo: GameResultRepository,
    @Autowired private val gameRepo: GameRepository
) {

    @GetMapping(path = ["/{gameId}"])
    fun getResultByGameId(@PathVariable gameId: Long) = gameRepo.getReferenceById(gameId).result

    @PostMapping(path = ["/{gameId}"])
    fun setResult(@RequestBody result: GameResult, @PathVariable gameId: Long): ResponseEntity<GameResult> {
        val assoc = gameRepo.getReferenceById(gameId)
        var saved: GameResult = repo.save(result)
        saved.game = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }
}