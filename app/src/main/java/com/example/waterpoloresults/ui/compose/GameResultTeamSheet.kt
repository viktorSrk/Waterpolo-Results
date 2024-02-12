package com.example.waterpoloresults.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme

@Composable
fun GameResultTeamSheet(
    teamSheet: Map<Int, String?>,
    goals: Map<Int, Int>,
    fouls: Map<Int, Int>,
    modifier: Modifier = Modifier) {


    LazyColumn(modifier = modifier) {
        item {
            TeamSheetHeader(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
        items(teamSheet.keys.toList()) {capNumber ->
            val playerName = teamSheet[capNumber] ?: "Unknown player"
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
    }
}

@Composable
fun PlayerRow(number: Int, name: String, goals: Int, fouls: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
        ) {
        Text(
            text = number.toString(),
            modifier = Modifier
                .padding(end = 16.dp)
                .width(20.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
        Text(
            text = goals.toString(),
            modifier = Modifier
                .padding(end = 16.dp)
                .width(20.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = fouls.toString(),
            modifier = Modifier
                .width(20.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TeamSheetHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nr.",
            modifier = Modifier
                .padding(end = 16.dp)
                .width(20.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Player Name",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.sports_waterpoloball),
            contentDescription = "Goals",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(end = 16.dp)
                .width(20.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.sports_whistle),
            contentDescription = "Goals",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .width(20.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultTeamSheetPreview() {
    WaterpoloResultsTheme {
        Surface {
            GameResultTeamSheet(
                teamSheet = mapOf(
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
                fouls = mapOf()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayerRowPreview() {
    WaterpoloResultsTheme {
        Surface {
            PlayerRow(
                number = 12,
                name = "Viktor Sersik",
                goals = 3,
                fouls = 1
            )
        }
    }
}
