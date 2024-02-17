package com.example.waterpoloresults.utils

import commons.League

fun groupDsvLeagues(leagues: Collection<League>): Map<String, List<League>> {
    return leagues.groupBy { it.dsvInfo?.dsvLeagueId ?: -1 }.mapKeys { it.value.first().name }
}

fun groupDsvLeaguesByKind(leagues: Collection<League>): Map<String, List<League>> {
    return leagues.groupBy { it.dsvInfo?.dsvLeagueKind ?: "" }
        .mapKeys { determineLeagueKind(it.key) }
}

fun determineLeagueKind(dsvKind: String): String {
    return when(dsvKind) {
        "L" -> LeagueKinds.LEAGUE
        "C" -> LeagueKinds.CUP
        "P" -> LeagueKinds.PLAYOFFS
        "V" -> LeagueKinds.PRELIM
        "Z" -> LeagueKinds.INTER
        else -> "Unknown"
    }
}

fun determineLeagueKind(league: League): String {
    return determineLeagueKind(league.dsvInfo?.dsvLeagueKind ?: "")
}

object LeagueKinds {
    const val LEAGUE = "League"
    const val CUP = "Cup"
    const val PLAYOFFS = "Playoffs"
    const val PRELIM = "Preliminary"
    const val INTER = "Intermediate"
}
