package server.api

import commons.GameResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameEventRepository
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

        val existingResult = assoc.result

        if (existingResult != null) {
            existingResult.apply {
                this.homeScore = result.homeScore
                this.awayScore = result.awayScore
                this.finished = result.finished
                this.gameEvents = result.gameEvents
                this.teamSheets = result.teamSheets
            }
            val updatedResult = repo.save(existingResult)
            return ResponseEntity.ok(updatedResult)
        } else {
            var saved: GameResult = repo.save(result)
            saved.game = assoc
            saved = repo.save(saved)
            return ResponseEntity.ok(saved)
        }
    }

    fun setFinished(resultId: Long): Boolean {
        val result = repo.getReferenceById(resultId)
        result.finished = true
        repo.save(result)
        return true
    }

    @PutMapping(path = ["/{gameId}"])
    fun updateResult(@RequestBody result: GameResult, @PathVariable gameId: Long): ResponseEntity<GameResult> {
        val assoc = gameRepo.getReferenceById(gameId)

        val existingResult = assoc.result
        if (existingResult != null)
            repo.delete(existingResult)

        var saved: GameResult = repo.save(result)
        saved.game = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }
}