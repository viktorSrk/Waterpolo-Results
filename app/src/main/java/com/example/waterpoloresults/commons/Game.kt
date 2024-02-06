package com.example.waterpoloresults.commons

import androidx.room.Embedded
import androidx.room.Entity
import java.util.Date

@Entity(
    tableName = "game",
    primaryKeys = ["leagueId", "leagueGroup", "leagueKind", "gameId", "season"])
data class Game(
    val leagueId: Int = -1,
    val leagueGroup: String = "",
    val leagueKind: String = "",
    val gameId: Int = -1,
    val season: Int = -1,
    val date: Long = -1,
    val homeTeam: String = "",
    val guestTeam: String = ""
    ) {

    fun buildGameLink(): String {
        return "Game.aspx?Season=" + season +
                "&LeagueID=" + leagueId +
                "&Group=" + leagueGroup +
                "&LeagueKind=" + leagueKind +
                "&GameID=" + gameId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (leagueId != other.leagueId) return false
        if (leagueGroup != other.leagueGroup) return false
        if (leagueKind != other.leagueKind) return false
        if (gameId != other.gameId) return false
        return season == other.season
    }

    override fun hashCode(): Int {
        var result = leagueId
        result = 31 * result + leagueGroup.hashCode()
        result = 31 * result + leagueKind.hashCode()
        result = 31 * result + gameId
        result = 31 * result + season
        return result
    }
}

data class GamesScraped(
    val games: MutableSet<Game> = mutableSetOf()
) {

}
