package server.database

import commons.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GameRepository : JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g " +
            "WHERE g.dsvInfo.dsvGameId = :gameId " +
            "AND g.league.dsvInfo.dsvLeagueSeason = :season " +
            "AND g.league.dsvInfo.dsvLeagueId = :leagueId " +
            "AND g.league.dsvInfo.dsvLeagueGroup = :group " +
            "AND g.league.dsvInfo.dsvLeagueKind = :kind ")
    fun findGameByDsvInfo(
        @Param("gameId") gameId: Int,
        @Param("season") season: Int,
        @Param("leagueId") leagueId: Int,
        @Param("group") group: String,
        @Param("kind") kind: String
    ): Game?
}