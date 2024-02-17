package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.League
import commons.LeagueDsvInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCard(
    countryName: String,
    leagues: Collection<League>,
    modifier: Modifier = Modifier,
    preferredOrder: List<String> = emptyList(),
    onLeagueClick: (List<League>) -> Unit = {}) {

    var expanded by remember { mutableStateOf(false) }

    val leaguesByRegion: Map<String, List<League>> = leagues.groupBy { it.region }
        .entries.sortedWith(compareBy({ preferredOrder.indexOf(it.key) == -1 }, { preferredOrder.indexOf(it.key) }, { it.key }))
        .associateBy({ it.key }, {it.value})

    Surface(modifier = modifier.padding(8.dp)) {
        Card(onClick = { expanded = !expanded }) {
            Text(
                text = countryName,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.headlineLarge
            )

            if (expanded) {
                Column(modifier = Modifier.padding(6.dp)) {
                    leaguesByRegion.forEach { (region, l) ->
                        RegionCard(
                            region,
                            l,
                            modifier = Modifier.padding(2.dp),
                            onLeagueClick = onLeagueClick
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview1(
    countryName: String = "DEU",
    leagues: Collection<League> = listOf(
        League(
            name = "Bundesliga",
            country = "DEU",
            region = "National",
            dsvInfo = LeagueDsvInfo(dsvLeagueId = 1)
        ),
        League(
            name = "Bundesligaaaaa",
            country = "DEU",
            region = "National",
            dsvInfo = LeagueDsvInfo(dsvLeagueId = 1)
        ),
        League(
            name = "Bundesliga Frauen",
            country = "DEU",
            region = "National",
            dsvInfo = LeagueDsvInfo(dsvLeagueId = 2)
        ),
        League(
            name = "2. Liga Nord",
            country = "DEU",
            region = "Landesgruppen"
        )
    )) {

    WaterpoloResultsTheme {
        Surface {
            CountryCard(countryName, leagues)
        }
    }
}