package com.example.waterpoloresults.ui.compose.game

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.waterpoloresults.R
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.TeamSheet
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameResultSheet(
    gameEvents: List<GameEvent>,
    teamSheets: List<TeamSheet>,
    modifier: Modifier = Modifier
) {
    val state = rememberPagerState(initialPage = 1) {
        3
    }
    val tabTitles = listOf("Home", "Events", "Away")
    val tabIcons = listOf(R.drawable.team_filled, R.drawable.timer, R.drawable.team_outline)

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = state.currentPage
        ) {
            val coroutineScope = rememberCoroutineScope()
            tabTitles.forEachIndexed { index, tab ->
                Tab(
                    selected = state.currentPage == index,
                    onClick = { coroutineScope.launch { state.animateScrollToPage(index) } },
//                    text = { Text(tab)},
                    icon = { Icon(painter = painterResource(id = tabIcons[index]), contentDescription = null) }
                )
            }
        }
        GameResultSheetPager(state = state, gameEvents = gameEvents, teamSheets = teamSheets, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameResultSheetPager(
    state: PagerState,
    gameEvents: List<GameEvent>,
    teamSheets: List<TeamSheet>,
    modifier: Modifier = Modifier
) {
    HorizontalPager(state = state, modifier = modifier, verticalAlignment = Alignment.Top) {pageIndex ->
        when(pageIndex) {
            1 -> {
                EventsSheet(gameEvents = gameEvents)
            }
            else -> {
                val teamSheetOfTeam = teamSheets[pageIndex/2]
                
                val playerNames = teamSheetOfTeam.players.map { it.number to it.name }.toMap()
                val coach = teamSheetOfTeam.coach

                val goals = gameEvents.filter { it is GoalGameEvent && it.scorerTeamHome == (pageIndex == 0) }
                    .groupBy { (it as GoalGameEvent).scorerNumber }
                    .mapValues { it.value.size }

                val fouls = mutableMapOf<Int, Int>()
                fouls.putAll(
                    gameEvents.filter{ it is ExclusionGameEvent && it.excludedTeamHome == (pageIndex == 0) }
                        .groupBy { (it as ExclusionGameEvent).excludedNumber }
                        .mapValues { it.value.size }
                )
                gameEvents.filter{it is PenaltyGameEvent && it.penalizedTeamHome == (pageIndex == 0) }
                    .groupBy { (it as PenaltyGameEvent).penalizedNumber }
                    .mapValues { it.value.size }
                    .forEach {(number, penalties) ->
                        fouls.merge(number, penalties) { v1, v2 -> v1 + v2 }
                    }

                val timeouts = gameEvents.filter{ it is TimeoutGameEvent && it.teamHome == (pageIndex == 0) }.size

                TeamStatsSheet(
                    playerNames = playerNames,
                    goals = goals,
                    fouls = fouls,
                    coach = coach,
                    timeouts = timeouts
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultSheetPreview() {
    WaterpoloResultsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GameResultSheet(
                gameEvents = listOf(
                    GameEvent(quarter = 1, time = 441),
                    GoalGameEvent(
                        quarter = 2, time = 321,
                        scorerTeamHome = false, scorerName = "Ein Lutscher", scorerNumber = 4
                    ),
                    GameEvent(quarter = 1, time = 398),
                    GameEvent(quarter = 3, time = 423),
                    GoalGameEvent(
                        quarter = 2, time = 345,
                        scorerTeamHome = true, scorerName = "J. Enwenaaaaaa", scorerNumber = 11
                    ),
                    GoalGameEvent(
                        scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12
                    ),
                    ExclusionGameEvent(
                        excludedTeamHome = true, excludedName = "F. Lenger", excludedNumber = 3
                    ),
                    PenaltyGameEvent(
                        penalizedTeamHome = true, penalizedName = "N. Sommer", penalizedNumber = 2
                    ),
                    TimeoutGameEvent(
                        teamHome = true
                    )
                ),
                teamSheets = listOf(
                    TeamSheet(
                        players = listOf(
                            TeamSheet.Player(number = 1, name = "Heins, Lasse"),
                            TeamSheet.Player(number = 2, name = "Sommer, Nils"),
                            TeamSheet.Player(number = 3, name = "Lenger, Fynn"),
                            TeamSheet.Player(number = 11, name = "Enwena, Joseph"),
                            TeamSheet.Player(number = 12, name = "Sersik, Viktor")
                        ),
                        coach = "Müller, Lutz"
                    ),
                    TeamSheet(
                        players = listOf(
                            TeamSheet.Player(number = 4, name = "Lutscher, Ein")
                        )
                    )
                )
            )
        }
    }
}
