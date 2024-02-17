package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(game: Game, modifier: Modifier = Modifier, onClick: (Long) -> Unit = {}) {
    Card(
        onClick = { onClick(game.id) },
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            ResultDateCard(game = game, modifier = Modifier.align(Alignment.CenterVertically))
            ResultTexts(home = game.home, away = game.away)
        }
    }
}

@Composable
fun ResultDateCard(game: Game, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.wrapContentWidth()) {
        if (game.result?.finished ?: false) {
            ResultTexts(
                home = game.result!!.homeScore.sum().toString(),
                away = game.result!!.awayScore.sum().toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Date(game.date, Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun ResultTexts(home: String, away: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            home,
            modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            away,
            modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Date(date: Long, modifier: Modifier = Modifier) {
    Text(
        text = SimpleDateFormat("dd. MMM.\nHH.mm", Locale.getDefault()).format(date),
        modifier = modifier.padding(8.dp),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameCardPreview(game: Game = Game(
    home = "Hamburger TB v. 1862",
    away = "SV Poseidon Hamburg",
    date = 1692549390100,
    result = GameResult(
        homeScore = arrayOf(2, 3, 1, 5),
        awayScore = arrayOf(0, 1, 2, 2),
        finished = true)
)) {

    WaterpoloResultsTheme {
        Surface {
            GameCard(game)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameCardPreview2() {
    GameCardPreview(game = Game(
        home = "Hamburger TB v. 1862",
        away = "SV Poseidon Hamburg",
        date = 1692549390100,
        result = GameResult(
            homeScore = arrayOf(2, 3, 1, 5),
            awayScore = arrayOf(0, 1, 2, 2),
            finished = false)))
}