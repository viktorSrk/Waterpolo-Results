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

    const val leagueIdArg = "league_id"
    val routeWithArg = "${route}/{${leagueIdArg}}"
    val arguments = listOf(
        navArgument(leagueIdArg) { type = NavType.LongType }
    )
}

object GameDestination : Destinations {
    override val name = "Game"
    override val route = "game"

    const val leagueIdArg = "league_id"
    const val gameIdArg = "game_id"
    val routeWithArg = "${LeagueDestination.route}/{${leagueIdArg}}/${route}/{${gameIdArg}}"
    val arguments = listOf(
        navArgument(leagueIdArg) { type = NavType.LongType },
        navArgument(gameIdArg) { type = NavType.LongType }
    )
}
