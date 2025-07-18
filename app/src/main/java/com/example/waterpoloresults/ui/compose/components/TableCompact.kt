package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TableCompact(
    positions: Map<String, Int>,
    mp: Map<String, Int>,
    pts: Map<String, Int>,
    dif: Map<String, Int>,
    modifier: Modifier = Modifier
) {

    val teamsListSorted = positions.toList().sortedBy{ it.second }.map{ it.first }

    Column(modifier = modifier) {
        TableHeader(modifier = Modifier.padding(8.dp))
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            teamsListSorted.forEach { team ->
                TableTeamRow(
                    modifier = Modifier.padding(8.dp),
                    position = positions[team] ?: 999,
                    team = team,
                    mp = mp[team] ?: 0,
                    pts = pts[team] ?: 0,
                    dif = dif[team] ?: 0
                )
            }
        }
    }
}

@Composable
fun TableTeamRow(
    position: Int,
    team: String,
    mp: Int,
    pts: Int,
    dif: Int,
    modifier: Modifier = Modifier
) {
    val teamStyle = MaterialTheme.typography.bodyLarge
    val statsMod = Modifier

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row {
                Text(text = position.toString(), style = teamStyle)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = team, style = teamStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Box {
            Row(
                modifier = Modifier.width(120.dp)
            ) {
                Text(text = mp.toString(), modifier = statsMod.weight(1f), style = teamStyle, textAlign = TextAlign.Center)
                Text(text = pts.toString(), modifier = statsMod.weight(1f), style = teamStyle, textAlign = TextAlign.Center)
                Text(text = (if (dif > 0) "+" else "") + dif.toString(), modifier = statsMod.weight(1f), style = teamStyle, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun TableHeader(modifier: Modifier = Modifier) {
    val titleMod = Modifier
    val titleStyle = MaterialTheme.typography.labelMedium

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Team", style = titleStyle)
        Box { Row(
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "MP", modifier = titleMod.weight(1f), style = titleStyle, textAlign = TextAlign.Center)
            Text(text = "Pts", modifier = titleMod.weight(1f), style = titleStyle, textAlign = TextAlign.Center)
            Text(text = "Dif.", modifier = titleMod.weight(1f), style = titleStyle, textAlign = TextAlign.Center)
        } }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TableCompositionPreview() {
    WaterpoloResultsTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TableCompact(
                positions = mapOf(
                    "Team D" to 4,
                    "Team A" to 1,
                    "Team B" to 2,
                    "Team E" to 5,
                    "Team C" to 3
                ),
                mp = mapOf(
                    "Team B" to 5,
                    "Team C" to 5,
                    "Team D" to 5,
                    "Team E" to 5,
                    "Team A" to 5,
                ),
                pts = mapOf(
                    "Team D" to 8,
                    "Team A" to 15,
                    "Team B" to 12,
                    "Team E" to 5,
                    "Team C" to 10
                ),
                dif = mapOf(
                    "Team C" to 2,
                    "Team D" to -3,
                    "Team B" to 5,
                    "Team A" to 100,
                    "Team E" to -14
                )
            )
        }
    }
}