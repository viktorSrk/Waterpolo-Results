package com.example.waterpoloresults

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.waterpoloresults.ui.compose.components.CountryCard
import com.example.waterpoloresults.ui.compose.game.GameScreen
import com.example.waterpoloresults.ui.compose.league.LeagueScreen
import commons.League

@Composable
fun MyNavHost(
    navController: NavHostController,
    leaguesState: List<League>,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LeaguesList.route,
        modifier = modifier
    ) {
        composable(LeaguesList.route) {
            LazyColumn {
                item {
                    CountryCard(
                        countryName = "DEU",
                        leagues = leaguesState,
                        modifier = Modifier.fillMaxWidth(),
                        preferredOrder = listOf("National", "Nord", "Ost", "West", "Süd", "Landesgruppen"),
                        onLeagueClick = { groupedLeagues ->
                            val arr = groupedLeagues.map { it.id }
                            val arrString = arr.joinToString(",")

                            navController.navigateSingleTopTo(
                                "${LeagueDestination.route}/${arrString}"
                            )
                        }
                    )
                }
            }
        }
        composable(
            route = LeagueDestination.routeWithArg,
            arguments = LeagueDestination.arguments
        ) {
            val leagueIdsString = it.arguments?.getString(LeagueDestination.leagueIdsStringArg)
            val leagueIds = leagueIdsString?.split(",")?.map { it.toLong() } ?: return@composable

            val leagues: List<League> = leaguesState.filter { it.id in leagueIds}

            LeagueScreen(
                leagues = leagues,
                onGameClick = { gameId ->
                    navController.navigateSingleTopTo(
                        "${LeagueDestination.route}/${leagueIdsString}/${GameDestination.route}/${gameId}"
                    )
                }
            )
        }
        composable(
            route = GameDestination.routeWithArg,
            arguments = GameDestination.arguments
        ) {
            val leagueIdsString = it.arguments?.getString(LeagueDestination.leagueIdsStringArg)
            val leagueIds = leagueIdsString?.split(",")?.map { it.toLong() } ?: return@composable
            val gameId = it.arguments?.getLong(GameDestination.gameIdArg)

            val game = leaguesState.filter { it.id in leagueIds }.flatMap{ it.games }.find { it.id == gameId }
                ?: return@composable

            GameScreen(
                game = game,
                result = game.result!!,
                gameEvents = game.result!!.gameEvents,
                teamSheets = game.result!!.teamSheets
            )
        }
    }
}

fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}