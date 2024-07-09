package server.api

import commons.League
import commons.LeagueDsvInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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

    @GetMapping(path = ["/{id}"])
    fun getLeagueById(@PathVariable id: Long): League = repo.getReferenceById(id)

    @GetMapping(path = ["/dsvInfo"])
    fun getLeagueByDsvInfo(@RequestBody dsvInfo: LeagueDsvInfo): League?
        = repo.findLeagueByDsvParameters(dsvInfo.dsvLeagueSeason,
                                         dsvInfo.dsvLeagueId,
                                         dsvInfo.dsvLeagueGroup,
                                         dsvInfo.dsvLeagueKind)

    @PostMapping(path = ["", "/"])
    fun addLeague(@RequestBody league: League): ResponseEntity<League> {
        if (getLeagues().contains(league)) {
            val existing = getLeagues().find { it == league }
            return ResponseEntity.ok(existing)
        }

        val saved: League = repo.save(league)
        if (league.dsvInfo != null) {
            setDsvInfo(league.dsvInfo!!, saved.id)
        }
        return ResponseEntity.ok(saved)
    }

    @PostMapping(path = ["/addDsvInfo/{leagueId}"])
    fun setDsvInfo(@RequestBody dsvInfo: LeagueDsvInfo, @PathVariable leagueId: Long): ResponseEntity<LeagueDsvInfo> {
        val assoc = repo.getReferenceById(leagueId)
        if (assoc.dsvInfo != null) {
            val toUpdate = assoc.dsvInfo
            toUpdate!!.dsvLeagueSeason = dsvInfo.dsvLeagueSeason
            toUpdate.dsvLeagueId = dsvInfo.dsvLeagueId
            toUpdate.dsvLeagueGroup = dsvInfo.dsvLeagueGroup
            toUpdate.dsvLeagueKind = dsvInfo.dsvLeagueKind
            val saved: LeagueDsvInfo = dsvRepo.save(toUpdate)
            return ResponseEntity.ok(saved)
        }
        var saved: LeagueDsvInfo = dsvRepo.save(dsvInfo)
        saved.league = assoc
        saved = dsvRepo.save(saved)
        return ResponseEntity.ok(saved)
    }
}