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
import commons.TeamSheet
import commons.gameevents.GoalGameEvent

@Composable
fun TopScorerList(
    leagues: List<League>,
    modifier: Modifier = Modifier
) {
    val games by remember { mutableStateOf(leagues.flatMap { it.games }) }

    val matchesPlayed = games.flatMap { it.result?.teamSheets ?: emptyList() }
        .flatMap { it.players }.map { it.name }
        .groupBy { it }.mapValues { it.value.size }

    val goalScorerList = getGoalScorerList(games)
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

fun getGoalScorerList(games: List<Game>): Map<String, Int> {
    val result = mutableMapOf<String, Int>()

    for (game in games) {
        val gameEvents = game.result?.gameEvents ?: emptyList()

        for (i in 0..1) {
            val team = game.result?.teamSheets?.get(i) ?: continue

            val playerNames = team.players.associate { it.number to it.name }
            val goals = gameEvents.filter { it is GoalGameEvent && it.scorerTeamHome == (i == 0) }
                .groupBy { (it as GoalGameEvent).scorerNumber }
                .mapValues { it.value.size }
            
            val scorers = playerNames.map { it.value to (goals[it.key] ?: 0)}.toMap()
                .filter { it.value > 0 }

            scorers.forEach {
                result.merge(it.key, it.value) { v1, v2 -> v1 + v2 }
            }
        }
    }

    return result
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
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 3", scorerNumber = 3),
                            ),
                            teamSheets = listOf(
                                TeamSheet(
                                    players = listOf(
                                        TeamSheet.Player(name = "Player 1", number = 1),
                                        TeamSheet.Player(name = "Player 2", number = 2),
                                        TeamSheet.Player(name = "Player 3", number = 3)
                                    )
                                ),
                                TeamSheet()
                            )
                        )
                    ),
                    Game(
                        result = GameResult(
                            gameEvents = listOf(
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                            ),
                            teamSheets = listOf(
                                TeamSheet(
                                    players = listOf(
                                        TeamSheet.Player(name = "Player 1", number = 1),
                                    )
                                ),
                                TeamSheet()
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
