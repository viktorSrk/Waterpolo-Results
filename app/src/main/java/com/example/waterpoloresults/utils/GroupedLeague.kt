package com.example.waterpoloresults.utils

import commons.League


fun groupDsvLeagues(leagues: Collection<League>) : Map<String, List<League>> {
    return leagues.groupBy { it.dsvInfo?.dsvLeagueId ?: -1 }.mapKeys { it.value.first().name }
}
