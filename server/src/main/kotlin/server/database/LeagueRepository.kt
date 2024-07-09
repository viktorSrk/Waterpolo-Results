package server.database

import commons.League
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LeagueRepository : JpaRepository<League, Long> {
    @Query("SELECT l from League l " +
            "WHERE l.dsvInfo.dsvLeagueSeason = :season " +
            "AND l.dsvInfo.dsvLeagueId = :id " +
            "AND l.dsvInfo.dsvLeagueGroup = :group " +
            "AND l.dsvInfo.dsvLeagueKind = :kind ")
    fun findLeagueByDsvParameters(@Param("season") season: Int,
                                  @Param("id") id: Int,
                                  @Param("group") group: String,
                                  @Param("kind") kind: String): League?
}