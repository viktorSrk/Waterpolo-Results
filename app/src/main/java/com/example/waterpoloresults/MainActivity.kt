package com.example.waterpoloresults

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.ServerUtils
import commons.League
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val SERVER_URL = "192.168.2.165:8080"
        val sut = ServerUtils(SERVER_URL)
    }

    private val leagues = mutableStateOf(listOf<League>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedLeagues = sut.getLeagues()
            leagues.value = fetchedLeagues
        }

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(leagues.value)
                }
            }
        }
    }
}

@Composable
fun Greeting(leagues: List<League>, modifier: Modifier = Modifier) {
    val preferredOrder = listOf("DEU National", "DEU Landesgruppen")
    val leaguesByRegion = leagues.groupBy { "${it.country} ${it.region}" }
        .entries
        .sortedWith(compareBy({ preferredOrder.indexOf(it.key) == -1 }, { preferredOrder.indexOf(it.key) }, { it.key }))
        .associateBy({ it.key }, {it.value})

    LazyColumn {
        leaguesByRegion.forEach { (countryAndRegion, leagues) ->
            item {
                Text(text = countryAndRegion,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp))
            }
            items(leagues) {l ->
                Button(onClick = { /*TODO*/}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = l.name)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val dummyLeagues = listOf(
        League(1, "League 1", "DEU", "Region 1"),
        League(2, "League 2", "DEU", "Region 2"),
        League(3, "League 3", "DEU", "Region 1"),
        League(4, "League 4", "DEU", "Region 2")
    )
    WaterpoloResultsTheme {
        Greeting(dummyLeagues)
    }
}