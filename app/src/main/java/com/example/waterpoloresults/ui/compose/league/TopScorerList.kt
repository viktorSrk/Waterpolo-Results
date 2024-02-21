package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.PlayerRow
import com.example.waterpoloresults.ui.compose.components.TopScorerHeader
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.League
import commons.gameevents.GoalGameEvent

@Composable
fun TopScorerList(
    leagues: List<League>,
    modifier: Modifier = Modifier
) {
    val games by remember { mutableStateOf(leagues.flatMap { it.games }) }

    //TODO: fix matchesPlayed
    val matchesPlayed = games.flatMap { it.result?.teamSheets ?: emptyList() }
        .flatMap { it.players }.map { it.name }
        .groupBy { it }.mapValues { it.value.size }

    //TODO: fix goalScorerList to be gotten from teamsheets instead of gameEvents
    val goalScorerList = games.flatMap { it.result?.gameEvents ?: emptyList() }
        .filter { it is GoalGameEvent }.map { it as GoalGameEvent }
        .groupBy { it.scorerName }
        .mapValues { it.value.size }
        .toList().sortedBy{ matchesPlayed[it.first] }.sortedByDescending { it.second }.toMap()

    Card(modifier = modifier.padding(8.dp)) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            item {
                TopScorerHeader(modifier = Modifier.padding(vertical = 8.dp).padding(bottom = 8.dp))
            }

            var previousGoals = -1
            var position = 1
            items(goalScorerList.keys.toList()) {scorer ->
                val goals = goalScorerList[scorer] ?: 0
                val matches = matchesPlayed[scorer] ?: 0
                if (goals != previousGoals) {
                    val index = goalScorerList.values.indexOf(goals)

                    position = index + 1
                    previousGoals = goals
                }

                PlayerRow(position, scorer, goals, matches, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopScorerListPreview() {
    val dummyLeagues =
        listOf(
            League(
                games = listOf(
                    Game(
                        result = GameResult(
                            gameEvents = listOf(
                                GoalGameEvent(scorerName = "Player 1"),
                                GoalGameEvent(scorerName = "Player 2"),
                                GoalGameEvent(scorerName = "Player 1"),
                                GoalGameEvent(scorerName = "Player 2"),
                                GoalGameEvent(scorerName = "Player 2"),
                                GoalGameEvent(scorerName = "Player 2"),
                                GoalGameEvent(scorerName = "Player 3")
                            ),
                            teamSheets = listOf(
                                commons.TeamSheet(
                                    players = listOf(
                                        commons.TeamSheet.Player(name = "Player 1"),
                                        commons.TeamSheet.Player(name = "Player 2"),
                                        commons.TeamSheet.Player(name = "Player 3")
                                    )
                                )
                            )
                        )
                    ),
                    Game(
                        result = GameResult(
                            gameEvents = listOf(
                                GoalGameEvent(scorerName = "Player 1"),
                                GoalGameEvent(scorerName = "Player 1")
                            ),
                            teamSheets = listOf(
                                commons.TeamSheet(
                                    players = listOf(
                                        commons.TeamSheet.Player(name = "Player 1")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

    WaterpoloResultsTheme {
        Surface {
            TopScorerList(leagues = dummyLeagues)
        }
    }
}
