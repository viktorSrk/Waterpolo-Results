package server.api

import commons.League
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.LeagueRepository

@RestController
@RequestMapping("/api/leagues")
class LeagueController(
    private val repo: LeagueRepository
) {

    @GetMapping(path = ["", "/"])
    fun getLeagues(): List<League> = repo.findAll()

    @GetMapping(path = ["/{id}"])
    fun getLeagueById(@PathVariable id: Long): ResponseEntity<League> {
        if (id <= 0 || !repo.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(repo.findById(id).get())
    }

    @PostMapping(path = ["", "/"])
    fun add(@RequestBody league: League): ResponseEntity<League> {
        val saved: League = repo.save(league)
        return ResponseEntity.ok(saved)
    }
}