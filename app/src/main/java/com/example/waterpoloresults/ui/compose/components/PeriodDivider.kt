package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeriodDivider(period: Int, modifier: Modifier = Modifier) {

    val periodString = when (period) {
        1 -> {"${period}st"}
        2 -> "${period}nd"
        3 -> "${period}rd"
        else -> "${period}th"
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Divider(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
        )
        Text(
            text = "$periodString Period",
            style = MaterialTheme.typography.labelSmall
        )
        Divider(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )
    }
}