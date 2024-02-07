package server.api

import commons.League
import commons.LeagueDsvInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import server.database.LeagueDsvInfoRepository
import server.database.LeagueRepository

@RestController
@RequestMapping("/api/leagues")
class LeagueController(
    private val repo: LeagueRepository,
    @Autowired private val dsvRepo: LeagueDsvInfoRepository
) {

    @GetMapping(path = ["", "/"])
    fun getLeagues(): List<League> = repo.findAll()

    @PostMapping(path = ["", "/"])
    fun add(@RequestBody league: League): ResponseEntity<League> {
        val saved: League = repo.save(league)
        return ResponseEntity.ok(saved)
    }

    @PostMapping(path = ["/addDsvInfo/{leagueId}"])
    fun updateDsvInfo(@RequestBody dsvInfo: LeagueDsvInfo, @PathVariable leagueId: Long): ResponseEntity<LeagueDsvInfo> {
        val assoc = repo.getReferenceById(leagueId)
        var saved: LeagueDsvInfo = dsvRepo.save(dsvInfo)
        saved.league = assoc
        saved = dsvRepo.save(saved)
        return ResponseEntity.ok(saved)
    }
}