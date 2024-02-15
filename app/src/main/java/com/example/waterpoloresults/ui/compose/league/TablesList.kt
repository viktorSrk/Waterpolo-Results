package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.TableCard
import com.example.waterpoloresults.ui.compose.components.TableCompact
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.TableInfo
import commons.Game
import commons.GameResult

@Composable
fun TablesList(games: List<Game>) {

    val tableInfo = TableInfo.createTable(games)

    val positions = tableInfo.positions
    val mp = tableInfo.mp
    val pts = tableInfo.pts
    val dif = tableInfo.dif

    LazyColumn {
        item {
            TableCard(
                modifier = Modifier.padding(8.dp),
                positions = positions, mp = mp, pts = pts, dif = dif
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 570)
@Preview(showBackground = true, widthDp = 320, heightDp = 570,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TableCompositionPreview() {
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
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TablesList(dummyGames)
        }
    }
}