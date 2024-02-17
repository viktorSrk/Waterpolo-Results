package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.TableCard
import com.example.waterpoloresults.ui.compose.components.TableDropdown
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.TableInfo
import com.example.waterpoloresults.utils.groupDsvLeaguesByKind
import commons.Game
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo

@Composable
fun TablesList(
    leagues: List<League>
) {
    val leaguesGroupedByKind = groupDsvLeaguesByKind(leagues)

    val tablesIndexState = remember { mutableStateOf("All") }
    val tablesIndex = tablesIndexState.value
    val tables =
        if (tablesIndex == "All") leagues
        else leaguesGroupedByKind[tablesIndex] ?: emptyList()
    val buttonString =
        if (tablesIndex == "All") "All Tables"
        else tablesIndex

    LazyColumn {
        if (leaguesGroupedByKind.keys.size > 1) {
            item {
                TableDropdown(tablesIndexState = tablesIndexState, tables = leaguesGroupedByKind, buttonString = buttonString)
            }
        }
        items(tables) { l ->
            val tableInfo = TableInfo.createTable(l.games)
            val positions = tableInfo.positions
            val mp = tableInfo.mp
            val pts = tableInfo.pts
            val dif = tableInfo.dif

            Text(
                text = "Group ${l.dsvInfo?.dsvLeagueGroup ?: "?"}",
                style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp)
            )
            TableCard(
                positions = positions,
                mp = mp,
                pts = pts,
                dif = dif,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 570)
@Preview(showBackground = true, widthDp = 320, heightDp = 570,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TableCompositionPreview() {
    val dummyLeagues = listOf(
        League(
            dsvInfo = LeagueDsvInfo(dsvLeagueGroup = "A", dsvLeagueKind = "V"),
            games = listOf(
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
        ),
        League(
            dsvInfo = LeagueDsvInfo(dsvLeagueGroup = "B", dsvLeagueKind = "V"),
            games = listOf(
                Game(
                    home = "Hildesheim",
                    away = "Laatzen",
                    date = 1700002003000,
                    result = GameResult(homeScore = arrayOf(2, 3, 1, 5), awayScore = arrayOf(0, 1, 2, 2), finished = true)
                ),
                Game(
                    home = "Georgsmarienhütte",
                    away = "Cuxhaven",
                    date = 1710002003000,
                    result = GameResult(finished = false)
                )
            )
        ),
        League(
            dsvInfo = LeagueDsvInfo(dsvLeagueGroup = "C", dsvLeagueKind = "Z"),
            games = listOf(
                Game(
                    home = "Hamburger Turnerbund v. 1862",
                    away = "SV Poseidon Hamburg",
                    date = 1700002003000,
                    result = GameResult(homeScore = arrayOf(2, 3, 1, 5), awayScore = arrayOf(0, 1, 2, 2), finished = true)
                ),
                Game(
                    home = "Hildesheim",
                    away = "Laatzen",
                    date = 1700002003000,
                    result = GameResult(homeScore = arrayOf(2, 3, 1, 5), awayScore = arrayOf(0, 1, 2, 2), finished = true)
                )
            )
        )
    )

    WaterpoloResultsTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TablesList(dummyLeagues)
        }
    }
}