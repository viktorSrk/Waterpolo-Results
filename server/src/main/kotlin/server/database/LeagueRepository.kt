package server.database

import commons.League
import org.springframework.data.jpa.repository.JpaRepository

interface LeagueRepository : JpaRepository<League, Long> {
}