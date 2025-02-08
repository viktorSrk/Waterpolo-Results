package server.api

import commons.TeamSheet
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.GameResultRepository
import server.database.TeamSheetRepository

@RestController
@RequestMapping("/api/games/teamsheets")
class TeamSheetController(
    private val repo: TeamSheetRepository,
    @Autowired private val resultRepo: GameResultRepository
) {

    @GetMapping(path = ["/{resultId}"])
    fun getTeamSheetsByResultId(@PathVariable resultId: Long): List<TeamSheet> {
        return resultRepo.getReferenceById(resultId).teamSheets
    }

    @PostMapping(path = ["/add/{resultId}"])
    @Transactional
    fun addTeamSheets(@RequestBody teamSheets: List<TeamSheet>, @PathVariable resultId: Long): ResponseEntity<List<TeamSheet>> {
        if (teamSheets.size != 2) return ResponseEntity.badRequest().build()

        val assoc = resultRepo.getReferenceById(resultId)

        val existingTeamSheets = assoc.teamSheets
        if (existingTeamSheets.isNotEmpty()) {
            repo.deleteAllByIdInBatch(existingTeamSheets.map { it.id })
        }

        val savedList = mutableListOf<TeamSheet>()

        for (i in 0..1) {
            var saved: TeamSheet = repo.save(teamSheets[i])
            saved.gameResult = assoc
            saved = repo.save(saved)
            savedList.add(saved)
        }

        return ResponseEntity.ok(savedList)
    }
}