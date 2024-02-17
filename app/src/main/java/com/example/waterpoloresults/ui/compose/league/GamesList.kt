package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.GameCard
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LeagueGamesList(groups: List<League>, modifier: Modifier = Modifier, onGameClick: (Long) -> Unit = {}) {
    if (groups.size == 1) {
        LazyColumn(modifier = modifier) {
            item {GamesList(games = groups.first().games, modifier = modifier, onGameClick = onGameClick)}
        }
        return
    }

    val allGames = groups.flatMap { it.games }

    var gamesIndexState = remember { mutableStateOf(-1) }
    val gamesIndex = gamesIndexState.value
    val games =
        if (gamesIndex == -1) allGames
        else groups[gamesIndex].games
    val buttonString =
        if (gamesIndex == -1) "All Games"
        else "Group " + (groups[gamesIndex].dsvInfo?.dsvLeagueGroup ?: "")

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        item {
            GamesDropdown(gamesIndexState = gamesIndexState, groups = groups, buttonString = buttonString)
        }

        item {
            GamesList(games = games, onGameClick = onGameClick)
        }
    }
}

@Composable
fun GamesList(games: List<Game>, modifier: Modifier = Modifier, onGameClick: (Long) -> Unit = {}) {

    val gamesByMonth = games.groupBy {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it.date)
    }.toSortedMap { o1, o2 ->
        val date1 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o1)!!
        val date2 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o2)!!
        date1.compareTo(date2)
    }.mapValues { it.value.sortedBy { a -> a.date } }

    Column(modifier = modifier) {
        gamesByMonth.forEach { (month, games) ->
//            item {
                Text(text = month, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
//            }
//            items(games) { g ->
            games.forEach { g ->
                GameCard(
                    game = g,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { onGameClick(g.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GamesPreview() {
    val dummyGames = listOf(
        Game(
            home = "Hamburger Turnerbund v. 1862",
            away = "SV Poseidon Hamburg",
            date = 1700002003000,
            result = GameResult(homeScore = arrayOf(2, 3, 1, 5), awayScore = arrayOf(0, 1, 2, 2), finished = true)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        )
    )

    val dummyLeagues = listOf(
        League(
            games = dummyGames,
            dsvInfo = LeagueDsvInfo(
                dsvLeagueGroup = "A"
            )
        ),
        League(
            dsvInfo = LeagueDsvInfo(
                dsvLeagueGroup = "B"
            )
        ),
        League(
            games = dummyGames,
            dsvInfo = LeagueDsvInfo(
                dsvLeagueGroup = "C"
            )
        )
    )

    WaterpoloResultsTheme {
        Surface {
            LeagueGamesList(dummyLeagues)
        }
    }
}