package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun PlayerRow(number: Int, name: String, goals: Int, fouls: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
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

@Composable
fun CoachRow(name: String, timeouts: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Coach",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Timeouts",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 1..timeouts) {
                    Icon(
                        painter = painterResource(id = R.drawable.sports_tactics),
                        contentDescription = "Timeout",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
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