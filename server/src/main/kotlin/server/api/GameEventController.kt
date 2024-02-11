package server.api

import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameEventRepository
import server.database.GameResultRepository

@RestController
@RequestMapping("/api/games/events")
class GameEventController(
    private val repo: GameEventRepository,
    @Autowired private val resultRepo: GameResultRepository
) {

    @GetMapping(path = ["/{resultId}"])
    fun getAllByResultId(@PathVariable resultId: Long): List<GameEvent> {
        return resultRepo.getReferenceById(resultId).gameEvents
    }

    @PostMapping(path = ["/add/{resultId}"])
    fun add(@RequestBody event: GameEvent, @PathVariable resultId: Long): ResponseEntity<GameEvent> {
        val assoc = resultRepo.getReferenceById(resultId)
        var saved: GameEvent = repo.save(event)
        saved.gameResult = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }

    @PostMapping(path = ["/addGoal/{resultId}"])
    fun addGoal(@RequestBody event: GoalGameEvent, @PathVariable resultId: Long): ResponseEntity<GoalGameEvent> {
        val assoc = resultRepo.getReferenceById(resultId)
        var saved: GoalGameEvent = repo.save(event)
        saved.gameResult = assoc
        saved = repo.save(saved)
        return ResponseEntity.ok(saved)
    }

    @PostMapping(path = ["/addAllEvents/{resultId}"])
    fun addAllEvents(@RequestBody events: List<GameEvent>, @PathVariable resultId: Long): ResponseEntity<List<GameEvent>> {
        val assoc = resultRepo.getReferenceById(resultId)
        val savedList = mutableListOf<GameEvent>()

        for (e in events) {
            when (e) {
                is GoalGameEvent -> {
                    var saved: GoalGameEvent = repo.save(e)
                    saved.gameResult = assoc
                    saved = repo.save(saved)
                    savedList.add(saved)
                }
                else -> {
                    var saved: GameEvent = repo.save(e)
                    saved.gameResult = assoc
                    saved = repo.save(saved)
                    savedList.add(saved)
                }
            }
        }

        return ResponseEntity.ok(savedList)
    }
}