package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commons.League

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionCard(
    regionName: String,
    leagues: Collection<League>,
    modifier: Modifier = Modifier,
    onLeagueClick: (League) -> Unit = {}) {

    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(onClick = { expanded = !expanded }, modifier = modifier.fillMaxWidth()) {
        Text(
            text = regionName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleLarge
        )

        if (expanded) {
            leagues.forEach { l ->
                LeagueEntry(l, onClick = onLeagueClick)
            }
        }
    }
}