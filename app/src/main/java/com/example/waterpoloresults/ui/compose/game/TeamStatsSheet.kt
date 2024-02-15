package com.example.waterpoloresults.ui.compose.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.components.CoachRow
import com.example.waterpoloresults.ui.compose.components.PlayerRow
import com.example.waterpoloresults.ui.compose.components.TeamSheetHeader
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme

@Composable
fun TeamStatsSheet(
    playerNames: Map<Int, String?>,
    goals: Map<Int, Int>,
    fouls: Map<Int, Int>,
    coach: String,
    timeouts: Int,
    modifier: Modifier = Modifier) {


    LazyColumn(modifier = modifier) {
        item {
            TeamSheetHeader(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(playerNames.keys.toList()) { capNumber ->
            val playerName = playerNames[capNumber] ?: "Unknown player"
            val goalsScored = goals[capNumber] ?: 0
            val foulsCommited = fouls[capNumber] ?: 0

            PlayerRow(
                number = capNumber,
                name = playerName,
                goals = goalsScored,
                fouls = foulsCommited,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        item {
            val coachName = if (coach.isNotEmpty()) coach else "Unknown coach"

            Divider(modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp))
            CoachRow(
                name = coachName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                timeouts = timeouts
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultTeamSheetPreview() {
    WaterpoloResultsTheme {
        Surface {
            TeamStatsSheet(
                playerNames = mapOf(
                    1 to "Lasse Heins",
                    2 to "Moritz Cantzler",
                    3 to "Kim Dröse",
                    5 to "Joris Obernesserrrrrrrrrrrrrrrrrrrr",
                    6 to "Tim Seefeld",
                    7 to "Edgar Pauli",
                    10 to "Bjarne Fischer",
                    11 to "Joseph Enwena",
                    12 to "Viktor Sersik"
                ),
                goals = mapOf(
                    3 to 1,
                    5 to 2,
                    6 to 1,
                    7 to 2,
                    11 to 12,
                    12 to 5
                ),
                fouls = mapOf(),
                coach = "Anders, Bernd",
                timeouts = 2
            )
        }
    }
}

