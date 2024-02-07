package com.example.waterpoloresults

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class GamesActivity : ComponentActivity() {

    companion object {
        val sut = MainActivity.sut
    }

    private val games = mutableStateOf(emptyList<Game>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val leagueId = intent.getLongExtra("leagueId", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedGames = sut.getLeagueById(leagueId).games
            games.value = fetchedGames
        }

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Games(games.value)
                }
            }
        }
    }
}

@Composable
fun Games(games: List<Game>, modifier: Modifier = Modifier) {

    val gamesByMonth = games.groupBy {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it.date)
    }.toSortedMap { o1, o2 ->
        val date1 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o1)!!
        val date2 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o2)!!
        date1.compareTo(date2)
    }

    LazyColumn(modifier = modifier) {
        gamesByMonth.forEach {(month, games) ->
            item {
                Text(text = month, style = MaterialTheme.typography.headlineMedium)
            }
            items(games) { g ->
                GameCard(game = g, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun GameCard(game: Game, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = game.home,
                modifier = Modifier
                    .width(150.dp)
                    .padding(8.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center)
            Text(text = SimpleDateFormat("dd.MMM.\nHH:mm", Locale.getDefault()).format(game.date),
                modifier = Modifier
                    .width(60.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiary)
            Text(text = game.away,
                modifier = Modifier
                    .width(150.dp)
                    .padding(8.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamesPreview() {
    val dummyGames = listOf(
        Game(1, "Hamburger Turnerbund v. 1862", "SV Poseidon Hamburg", 100002003000),
        Game(2, "SV Poseidon Hamburg", "Hamburger Turnerbund v. 1862", 0L)
    )
    WaterpoloResultsTheme {
        Games(dummyGames)
    }
}