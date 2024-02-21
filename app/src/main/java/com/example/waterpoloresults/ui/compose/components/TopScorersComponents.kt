package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R

@Composable
fun GPGScorerRow(
    number: Int,
    name: String,
    goals: Float,
    modifier: Modifier = Modifier,
) {
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
        var goalsString = goals.toString()
        goalsString = if (goalsString.length >= 4) goalsString.substring(0, 4)
        else "${goalsString}0"
        Text(
            text = goalsString,
            modifier = Modifier
                .padding(end = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TopScorerHeader(
    modifier: Modifier = Modifier,
    gpg: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Player Name",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        var goalsModifier = Modifier
            .padding(end = 16.dp)
        goalsModifier = if (gpg) goalsModifier.padding(start = 16.dp).height(20.dp)
                        else goalsModifier.width(20.dp)
        Icon(
            painter = painterResource(id = R.drawable.sports_waterpoloball),
            contentDescription = "Goals",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = goalsModifier
        )

        if (!gpg) {
            Icon(
                painter = painterResource(id = R.drawable.sports_appearance),
                contentDescription = "Goals",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .width(20.dp)
            )
        }
    }
}
