package com.example.waterpoloresults.ui.compose.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.GameInformation
import com.example.waterpoloresults.ui.compose.components.GameResultBig
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.League

@Composable
fun GameResultOverview(game: Game, result: GameResult, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        LazyColumn {
            item { GameResultBig(game, result) }
            item { GameInformation(game, modifier = Modifier.padding(vertical = 8.dp)) }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultOverviewPreview() {
    WaterpoloResultsTheme {
        Surface {
            GameResultOverview(
                game = Game(
                    home = "Hamburger TB v. 1862",
                    away = "SV Poseidon",
                    league = League(
                        country = "DEU",
                        region = "Hamburg",
                        name = "Hamburger Liga")
                ),
                result = GameResult(
                    homeScore = arrayOf(2, 3, 1, 5),
                    awayScore = arrayOf(0, 1, 2, 2),
                    finished = true)
            )
        }
    }
}

