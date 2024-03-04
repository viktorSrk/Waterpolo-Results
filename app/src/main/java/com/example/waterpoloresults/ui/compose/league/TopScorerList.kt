package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.GPGScorerRow
import com.example.waterpoloresults.ui.compose.components.PlayerRow
import com.example.waterpoloresults.ui.compose.components.ScorerRow
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
    var gpg by remember { mutableStateOf(false) }

    val matchesPlayed = games.flatMap { it.result?.teamSheets ?: emptyList() }
        .flatMap { it.players }.map { it.name }
        .groupBy { it }.mapValues { it.value.size }

    val teamForPlayer = getTeamForPlayers(games)

    val goalScorerList = getGoalScorerList(games)
        .toList().sortedBy{ matchesPlayed[it.first] }.sortedByDescending { it.second }.toMap()

    val gpgScorerList: Map<String, Float> = goalScorerList.mapValues { it.value.toFloat() / (matchesPlayed[it.key] ?: 1) }
        .toList().sortedByDescending { it.second }.toMap()

    LazyColumn(modifier = modifier) {
        item {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goals Per Game",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
                Switch(checked = gpg, onCheckedChange = { gpg = it })
            }
        }
        item{
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    TopScorerHeader(
                        gpg = gpg,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(bottom = 8.dp)
                    )

                    var previousGoals: Float = -1f
                    var position = 1
                    if (gpg) {
                        gpgScorerList.keys.forEach { scorer ->
                            val team = teamForPlayer[scorer] ?: ""
                            val goals = gpgScorerList[scorer] ?: 0f
                            if (goals != previousGoals) {
                                val index = gpgScorerList.values.indexOf(goals)

                                position = index + 1
                                previousGoals = goals
                            }

                            GPGScorerRow(
                                number = position,
                                name = scorer,
                                team = team,
                                goals = goals,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        goalScorerList.keys.forEach { scorer ->
                            val team = teamForPlayer[scorer] ?: ""
                            val goals = goalScorerList[scorer] ?: 0
                            val matches = matchesPlayed[scorer] ?: 0
                            if (goals.toFloat() != previousGoals) {
                                val index = goalScorerList.values.indexOf(goals)

                                position = index + 1
                                previousGoals = goals.toFloat()
                            }

                            ScorerRow(
                                position,
                                scorer,
                                team,
                                goals,
                                matches,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
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

fun getTeamForPlayers(games: List<Game>): Map<String, String> {
    val result = mutableMapOf<String, String>()

    for (game in games) {
        val teamSheets = game.result?.teamSheets ?: emptyList()

        for (i in 0..1) {
            val teamName = if (i == 0) game.home else game.away
            val team = teamSheets[i]

            val playerNames = team.players.map{ it.name }
            playerNames.forEach { result[it] = teamName }
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
                        home = "Team 1",
                        away = "Team 2",
                        result = GameResult(
                            gameEvents = listOf(
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 1", scorerNumber = 1),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 2", scorerNumber = 2),
                                GoalGameEvent(scorerName = "Player 3", scorerNumber = 3),
                            ),
                            teamSheets = listOf(
                                TeamSheet(
                                    players = listOf(
                                        TeamSheet.Player(name = "Player 113123123123123123123123123123123123123", number = 1),
                                        TeamSheet.Player(name = "Player 2", number = 2),
                                        TeamSheet.Player(name = "Player 3", number = 3)
                                    )
                                ),
                                TeamSheet()
                            )
                        )
                    ),
                    Game(
                        home = "Team 1",
                        away = "Team 2",
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
