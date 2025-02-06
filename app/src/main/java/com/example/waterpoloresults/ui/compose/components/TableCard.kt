package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme

@Composable
fun TableCard(
    positions: Map<String, Int>,
    mp: Map<String, Int>,
    pts: Map<String, Int>,
    dif: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        TableCompact(
            positions = positions,
            mp = mp,
            pts = pts,
            dif = dif,
            modifier = Modifier.padding(8.dp).padding(horizontal = 2.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Preview(showBackground = true, widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TableCardPreview() {
    WaterpoloResultsTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TableCard(
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
                    "Team A" to 10,
                    "Team E" to -14
                )
            )
        }
    }
}
