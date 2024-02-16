package com.example.waterpoloresults

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destinations {
    val name: String
    val route: String
}

object LeaguesList : Destinations {
    override val name = "Leagues"
    override val route = "leaguesList"
}

object LeagueDestination : Destinations {
    override val name = "League"
    override val route = "league"

    const val leagueIdsStringArg = "league_id"
    val routeWithArg = "${route}/{${leagueIdsStringArg}}"
    val arguments = listOf(
        navArgument(leagueIdsStringArg) { type = NavType.StringType }
    )
}

object GameDestination : Destinations {
    override val name = "Game"
    override val route = "game"

    const val leagueIdsStringArg = "league_id"
    const val gameIdArg = "game_id"
    val routeWithArg = "${LeagueDestination.route}/{${leagueIdsStringArg}}/${route}/{${gameIdArg}}"
    val arguments = listOf(
        navArgument(leagueIdsStringArg) { type = NavType.StringType },
        navArgument(gameIdArg) { type = NavType.LongType }
    )
}
