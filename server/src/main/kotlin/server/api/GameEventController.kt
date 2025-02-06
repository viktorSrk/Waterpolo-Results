package server.api

import commons.GameResult
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    @PostMapping(path = ["/addAllEvents/{resultId}"])
    fun addAllEvents(@RequestBody events: List<GameEvent>, @PathVariable resultId: Long): ResponseEntity<List<GameEvent>> {
        val assoc: GameResult
        try {
            assoc = resultRepo.getReferenceById(resultId)
        } catch (e: Exception) {
            println("GameResult not found")
            return ResponseEntity.badRequest().build()
        }

        val existingEvents = assoc.gameEvents
        if (existingEvents.isNotEmpty()) {
            repo.deleteAllByIdInBatch(existingEvents.map { it.id });
        }

        val savedList = mutableListOf<GameEvent>()

        for (e in events) {
            when (e) {
                is GoalGameEvent -> {
                    var saved: GoalGameEvent = repo.save(e)
                    saved.gameResult = assoc
                    saved = repo.save(saved)
                    savedList.add(saved)
                }
                is ExclusionGameEvent -> {
                    var saved: ExclusionGameEvent = repo.save(e)
                    saved.gameResult = assoc
                    saved = repo.save(saved)
                    savedList.add(saved)
                }
                is PenaltyGameEvent -> {
                    var saved: PenaltyGameEvent = repo.save(e)
                    saved.gameResult = assoc
                    saved = repo.save(saved)
                    savedList.add(saved)
                }
                is TimeoutGameEvent -> {
                    var saved: TimeoutGameEvent = repo.save(e)
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