package com.example.waterpoloresults.commons

import androidx.room.Entity

@Entity(primaryKeys = ["leagueId", "group", "leagueKind"])
data class League(
    val name: String?,
    val leagueId: Int = -1,
    val group: String = "",
    val leagueKind: String = "",
    val region: String?) {

    fun buildLeagueLink(year: Int): String {
        return "https://dsvdaten.dsv.de/Modules/WB/League.aspx?Season=" + year +
                "&LeagueID=" + leagueId +
                "&Group=" + group +
                "&LeagueKind=" + leagueKind
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as League

        if (leagueId != other.leagueId) return false
        if (group != other.group) return false
        return leagueKind == other.leagueKind
    }

    override fun hashCode(): Int {
        var result = leagueId
        result = 31 * result + group.hashCode()
        result = 31 * result + leagueKind.hashCode()
        return result
    }
}

data class LeaguesScraped(
    val leagues: MutableSet<League> = mutableSetOf()
) {

}