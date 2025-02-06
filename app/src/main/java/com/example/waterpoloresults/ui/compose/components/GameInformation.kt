package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commons.Game
import java.text.SimpleDateFormat
import java.util.Locale

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