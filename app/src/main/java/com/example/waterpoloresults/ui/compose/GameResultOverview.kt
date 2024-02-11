package com.example.waterpoloresults.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.League
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GameResultOverview(game: Game, result: GameResult, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        LazyColumn {
            item { GameResultBig(game, result) }
            item { GameInformation(game, modifier = Modifier.padding(vertical = 8.dp)) }
        }
    }
}

@Composable
fun GameResultBig(game: Game, result: GameResult, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround) {
            TeamBig(game.home, modifier = Modifier.requiredWidth(120.dp))
            ResultBig(result = result, modifier = Modifier.align(Alignment.CenterVertically))
            TeamBig(game.away, modifier = Modifier.requiredWidth(120.dp))
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            for (i in 0..3) {
                ResultQuarter(result = result, quarter = i + 1)
            }
        }
    }
}

@Composable
fun TeamBig(
    teamName: String,
    teamBadge: Painter = painterResource(id = R.drawable.ic_launcher_foreground),
    modifier: Modifier = Modifier) {
    
    Column(modifier = modifier) {
        ElevatedCard(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = teamBadge,
                contentDescription = "Badge of $teamName",)
        }
        Text(
            text = teamName,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ResultBig(
    result: GameResult,
    modifier: Modifier = Modifier) {

    Card(modifier = modifier) {
        Text(
            text = "${result.homeScore.sum()} - ${result.awayScore.sum()}",
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun ResultQuarter(
    result: GameResult,
    quarter: Int,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Text(
            text = "${result.homeScore[quarter - 1]} - ${result.awayScore[quarter - 1]}",
            modifier = Modifier.padding(6.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun GameInformation(game: Game, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        GameInformationRow(
            label = "Date",
            value = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(game.date),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        GameInformationRow(
            label = "Country",
            value = game.league?.country ?: "Unknown",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        GameInformationRow(
            label = "Region",
            value = game.league?.region ?: "Unknown",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
fun GameInformationRow(label: String, value: String, modifier: Modifier = Modifier) {
    Divider(modifier = modifier)
    Row(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
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

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultBigPreview() {
    WaterpoloResultsTheme {
        Surface {
            GameResultBig(
                game = Game(
                    home = "Hamburger TB v. 1862",
                    away = "SV Poseidon"
                ),
                result = GameResult(
                    homeScore = arrayOf(2, 3, 1, 5),
                    awayScore = arrayOf(0, 1, 2, 2),
                    finished = true
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TeamBigPreview() {
    WaterpoloResultsTheme {
        Surface {
            TeamBig("Hamburger TB v. 1862")
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResultQuarterPreview() {
    WaterpoloResultsTheme {
        Surface {
            ResultQuarter(result = GameResult(
                homeScore = arrayOf(2, 3, 1, 5),
                awayScore = arrayOf(0, 1, 2, 2),
                finished = true), quarter = 1)
        }
    }
}
