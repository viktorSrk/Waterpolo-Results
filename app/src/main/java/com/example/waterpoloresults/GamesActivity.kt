package com.example.waterpoloresults

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.commons.Game
import com.example.waterpoloresults.commons.League
import com.example.waterpoloresults.databases.AppDatabase
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Locale

class GamesActivity : ComponentActivity() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MainActivity.database

        val leagueId = intent.getIntExtra("leagueId", -1)
        val leagueGroup = intent.getStringExtra("leagueGroup") ?: ""
        val leagueKind = intent.getStringExtra("leagueKind") ?: ""
        val season = intent.getIntExtra("season", 2023)

        val league = League(leagueId = leagueId, group = leagueGroup, leagueKind = leagueKind)

        val scraperUrl = intent.getStringExtra("scraperUrl") ?: ""
        val scraper = Scraper(scraperUrl)

        lifecycleScope.launch(Dispatchers.IO) {
            scrapeGamesOfLeagueSeason(league = league, season = season,
                scraper = scraper, context = this@GamesActivity, retries = 5)
        }

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var games by remember { mutableStateOf(emptyList<Game>()) }
                    LaunchedEffect(Unit) {
                        MainActivity.database.gameDao().getAllGamesOfLeagueSeason(
                            leagueId = leagueId,
                            leagueGroup = leagueGroup,
                            leagueKind = leagueKind,
                            season = season
                        ).collect {
                            games = it
                        }
                    }
                    Games(games = games, scraperUrl = scraperUrl, context = this@GamesActivity)
                }
            }
        }
    }
}

@Composable
fun Games(games: List<Game>,
          scraperUrl: String = "",
          context: Context = GamesActivity()) {

    val gamesByMonth = games.groupBy {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it.date)
    }.toSortedMap { o1, o2 ->
        val date1 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o1)!!
        val date2 = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(o2)!!
        date1.compareTo(date2)
    }

    LazyColumn {
        gamesByMonth.forEach { (month, games) ->
            item {
                Text(text = month, style = MaterialTheme.typography.headlineMedium)
            }
            items(games.sortedBy { it.date }) { g ->
                GameCard(g)
            }
        }
    }
}

@Composable
fun GameCard(game: Game) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = game.homeTeam,
                modifier = Modifier
                    .width(150.dp)
                    .padding(8.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center)
            Text(text = SimpleDateFormat("dd.\nMMM.", Locale.getDefault()).format(game.date),
                modifier = Modifier
                    .width(60.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiary)
            Text(text = game.guestTeam,
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
fun GameCardPreview() {
    val dummyGame = Game(1, "", "", 1, 2023, homeTeam = "Hamburger Turnerbund v. 1862", guestTeam = "SV Poseidon Hamburg", date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse("01.01.2022")!!.time)

    WaterpoloResultsTheme {
        GameCard(dummyGame)
    }
}

suspend fun scrapeGamesOfLeagueSeason(
    league: League, season: Int,
    scraper: Scraper, context: Context, retries: Int = 3) {

    try {
        val games = scraper.scrapeGames(league, season)
        GamesActivity.database.gameDao().insertAll(*games.games.toTypedArray())
    } catch (e: SocketTimeoutException) {
        if (retries > 0) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Sync failed, trying again...", Toast.LENGTH_SHORT).show()
            }
            scrapeGamesOfLeagueSeason(league, season, scraper, context, retries = retries - 1)
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Sync failed multiple times, please check network connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}