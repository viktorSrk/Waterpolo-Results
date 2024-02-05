package com.example.waterpoloresults.commons

data class League(
    val name: String = "",
    val leagueId: Int = -1,
    val leagueKind: String = "",
    val region: String = "") {

    fun buildLeagueLink(year: Int): String {
        return "https://dsvdaten.dsv.de/Modules/WB/League.aspx?Season=" + year +
                "&LeagueID=" + leagueId +
                "&Group=&LeagueKind=" + leagueKind
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as League

        if (leagueId != other.leagueId) return false
        return leagueKind == other.leagueKind
    }

    override fun hashCode(): Int {
        var result = leagueId
        result = 31 * result + leagueKind.hashCode()
        return result
    }
}

data class LeaguesScraped(
    val leagues: MutableSet<League> = mutableSetOf()
) {

}