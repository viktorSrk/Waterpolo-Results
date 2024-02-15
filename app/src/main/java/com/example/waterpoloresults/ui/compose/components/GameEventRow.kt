package com.example.waterpoloresults.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent

@Composable
fun GameEventRow(event: GameEvent, modifier: Modifier = Modifier) {
    var specificTeamEvent = true
    var homeTeamEvent = true
    when (event) {
        is GoalGameEvent -> {
            homeTeamEvent = event.scorerTeamHome
        }
        is ExclusionGameEvent -> {
            homeTeamEvent = event.excludedTeamHome
        }
        is PenaltyGameEvent -> {
            homeTeamEvent = event.penalizedTeamHome
        }
        is TimeoutGameEvent -> {
            homeTeamEvent = event.teamHome
        }
        else -> {
            specificTeamEvent = false
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        GameEventText(
            event = event,
            show = !specificTeamEvent || homeTeamEvent,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )

        GameEventType(event = event, show = !specificTeamEvent || homeTeamEvent)
        GameEventTime(event = event)
        GameEventType(event = event, show = !specificTeamEvent || !homeTeamEvent)

        GameEventText(
            event = event,
            show = !specificTeamEvent || !homeTeamEvent,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            arrangement = Arrangement.End
        )
    }
}

@Composable
fun GameEventTime(event: GameEvent, modifier: Modifier = Modifier) {
    val minutes: Long = event.time.floorDiv(60)
    val seconds: Long = event.time % 60
    val secondsString = if (seconds < 10) "0$seconds" else "$seconds"

    Text(
        text = "$minutes:$secondsString",
        modifier = modifier.padding(4.dp),
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun GameEventType(event: GameEvent, modifier: Modifier = Modifier, show: Boolean = true) {
    var iconDrawable = -1
    var iconTint = MaterialTheme.colorScheme.onSurfaceVariant

    when (event) {
        is GoalGameEvent -> {
            iconDrawable = R.drawable.sports_waterpoloball
            iconTint = MaterialTheme.colorScheme.primary
        }
        is ExclusionGameEvent -> {
            iconDrawable = R.drawable.sports_whistle
        }
        is PenaltyGameEvent -> {
            iconDrawable = R.drawable.sports_5m
        }
        is TimeoutGameEvent -> {
            iconDrawable = R.drawable.sports_tactics
        }
        else -> {}
    }

    if (show && iconDrawable != -1) {
        Icon(
            painter = painterResource(iconDrawable),
            tint = iconTint,
            contentDescription = "EventTypeIcon",
            modifier = modifier.size(20.dp)
        )
    } else {
        Spacer(modifier = modifier.size(20.dp))
    }
}

@Composable
fun GameEventText(event: GameEvent, modifier: Modifier = Modifier, show: Boolean = true, arrangement: Arrangement.Horizontal = Arrangement.Start) {

    var primaryText = ""
    var secondaryText = ""

    if (show) {
        when (event) {
            is GoalGameEvent -> {
                primaryText = event.scorerName
                secondaryText = "(${event.scorerNumber})"
            }
            is ExclusionGameEvent -> {
                primaryText = event.excludedName
                secondaryText = "(${event.excludedNumber})"
            }
            is PenaltyGameEvent -> {
                primaryText = event.penalizedName
                secondaryText = "(${event.penalizedNumber})"
            }
            is TimeoutGameEvent -> {
                primaryText = "Timeout"
            }
            else -> {}
        }

        Row(
            modifier = modifier,
            horizontalArrangement = arrangement
        ) {
            Text(
                text = "$secondaryText ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = primaryText,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    } else {
        Spacer(modifier = modifier)
    }
}