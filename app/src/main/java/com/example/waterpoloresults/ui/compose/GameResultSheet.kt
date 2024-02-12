package com.example.waterpoloresults.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.TeamSheet
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent

@Composable
fun GameResultSheet(
    gameEvents: List<GameEvent>,
    teamSheets: List<TeamSheet>,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(1) }
    val tabTitles = listOf("Home", "Events", "Away")
    val tabIcons = listOf(R.drawable.team_filled, R.drawable.timer, R.drawable.team_outline)

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = state
        ) {
            tabTitles.forEachIndexed { index, tab ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
//                    text = { Text(tab)},
                    icon = { Icon(painter = painterResource(id = tabIcons[index]), contentDescription = null) }
                )
            }
        }
        if (state == 1) {
            GameResultEventsSheet(gameEvents = gameEvents, modifier = Modifier.padding(8.dp))
        } else {
            val playerNames = teamSheets[state/2].players.map { it.number to it.name }.toMap()

            val goalsMap = gameEvents.filterIsInstance<GoalGameEvent>()
                .filter { it.scorerTeamHome == (state == 0)}
                .groupBy { it.scorerNumber }
                .mapValues { it.value.size }

            val exclusionsMap = gameEvents.filterIsInstance<ExclusionGameEvent>()
                .filter { it.excludedTeamHome == (state == 0)}
                .groupBy { it.excludedNumber }
                .mapValues { it.value.size }
            val penaltiesMap = gameEvents.filterIsInstance<PenaltyGameEvent>()
                .filter { it.penalizedTeamHome == (state == 0)}
                .groupBy { it.penalizedNumber }
                .mapValues { it.value.size }
            val foulsMap = mutableMapOf<Int, Int>()
            foulsMap.putAll(exclusionsMap)
            penaltiesMap.forEach { (number, penalties) ->
                foulsMap.merge(number, penalties) { v1, v2 -> v1 + v2 }
            }

            GameResultTeamSheet(
                playerNames = playerNames,
                goals = goalsMap,
                fouls = foulsMap,
                modifier = Modifier.padding(8.dp)
            )
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
                        )
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
