package com.example.waterpoloresults

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.waterpoloresults.commons.League
import com.example.waterpoloresults.databases.AppDatabase
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var database: AppDatabase
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Hello World from MainActivity", Toast.LENGTH_SHORT).show()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "waterpolo-results-database",
        ).build()
        val dsvScraper = Scraper("https://dsvdaten.dsv.de/Modules/WB/")

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier/*.fillMaxSize()*/, color = MaterialTheme.colorScheme.background) {
                    Column {
                        UpdateLeagues(scraper = dsvScraper, context = this@MainActivity)

                        var leagues by remember { mutableStateOf(emptyList<League>()) }
                        LaunchedEffect(Unit) {
                            database.leagueDao().getAll().collect {
                                leagues = it
                            }
                        }
                        Leagues(leagues)
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateLeagues(scraper: Scraper, context: Context) {
    val coroutinesScope = rememberCoroutineScope()
    Button(onClick = {
        coroutinesScope.launch {
            scrapeLeagues(scraper, context)
        }
    }) {
        Text(text = "Sync")
    }
}

suspend fun scrapeLeagues(scraper: Scraper, context: Context, retries: Int = 0) {
    try {
        val leagues = scraper.scrapeLeagues()
        MainActivity.database.leagueDao().insertAll(*leagues.leagues.toTypedArray())
    } catch (e: SocketTimeoutException) {
        if (retries < 3) {
            Toast.makeText(context, "Sync failed, trying again...", Toast.LENGTH_SHORT).show()
            scrapeLeagues(scraper, context, retries = retries + 1)
        } else {
            Toast.makeText(context, "Sync failed multiple times, please check network connection", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Leagues(leagues: List<League>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = leagues) { l ->
            Text(text = l.name!!)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateLeaguesPreview() {
    WaterpoloResultsTheme {
        UpdateLeagues(Scraper(""), MainActivity())
    }
}

@Preview(showBackground = true)
@Composable
fun LeaguesPreview() {
    val dummyLeagues = listOf(
        League("2. Liga Nord", 1, "", "M", "Nord"),
        League("2. Liga Süd", 2, "", "M", "Süd"),
        League("2. Liga Ost", 3, "",  "F", "Ost"),
        League("2. Liga West", 4, "",  "F", "West")
    )
    WaterpoloResultsTheme {
        Leagues(dummyLeagues)
    }
}