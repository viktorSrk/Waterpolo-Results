package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.GameCard
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GamesList(games: List<Game>, modifier: Modifier = Modifier, onGameClick: (Long) -> Unit = {}) {

    val gamesByMonth = games.groupBy {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it.date)
    }.toSortedMap { o1, o2 ->
        val date1 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o1)!!
        val date2 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o2)!!
        date1.compareTo(date2)
    }.mapValues { it.value.sortedBy { a -> a.date } }

    LazyColumn(modifier = modifier) {
        gamesByMonth.forEach { (month, games) ->
            item {
                Text(text = month, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
            }
            items(games) { g ->
                GameCard(
                    game = g,
                    modifier = Modifier.fillMaxWidth(),
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
        )
    )
    WaterpoloResultsTheme {
        Surface {
            GamesList(dummyGames)
        }
    }
}