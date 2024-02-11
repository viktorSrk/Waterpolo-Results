package com.example.waterpoloresults.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.R
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent

@Composable
fun GameResultEventsSheet(
    gameEvents: List<GameEvent>,
    modifier: Modifier = Modifier
){

    val gameEventsPerQuarter = gameEvents.groupBy { it.quarter }
        .toSortedMap { a, b -> a.compareTo(b)}
        .mapValues { it.value.sortedByDescending { e -> e.time } }

    LazyColumn(modifier = modifier) {
        gameEventsPerQuarter.forEach { (period, events) ->
            item {
                PeriodDivider(period = period, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            }
            items(events) { e ->
                GameEventRow(event = e, modifier = Modifier.padding(8.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
fun PeriodDivider(period: Int, modifier: Modifier = Modifier) {

    val periodString = when (period) {
        1 -> {"${period}st"}
        2 -> "${period}nd"
        3 -> "${period}rd"
        else -> "${period}th"
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Divider(modifier = Modifier
            .padding(end = 8.dp)
            .weight(1f))
        Text(
            text = "$periodString Period",
            style = MaterialTheme.typography.labelSmall
        )
        Divider(modifier = Modifier
            .padding(start = 8.dp)
            .weight(1f))
    }
}

@Composable
fun GameEventRow(event: GameEvent, modifier: Modifier = Modifier) {
    var specificTeamEvent = false
    var homeTeamEvent = true
    when (event) {
        is GoalGameEvent -> {
            specificTeamEvent = true
            homeTeamEvent = event.scorerTeamHome
        }
        else -> {}
    }

    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {

        GameEventText(
            event = event,
            show = !specificTeamEvent || homeTeamEvent,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )

        GameEventType(event = event, show = !specificTeamEvent || homeTeamEvent)
        GoalEventTime(event = event)
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
fun GoalEventTime(event: GameEvent, modifier: Modifier = Modifier) {
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
    var iconTint = MaterialTheme.typography.labelLarge.color

    when (event) {
        is GoalGameEvent -> {
            iconDrawable = R.drawable.sports_waterpoloball
            iconTint = MaterialTheme.colorScheme.primary
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

            else -> {}
        }

        Row(
            modifier = modifier,
            horizontalArrangement = arrangement) {
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

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultEventsSheetPreview() {
    WaterpoloResultsTheme {
        GameResultEventsSheet(
            gameEvents = listOf(
                GameEvent(quarter = 1, time = 441),
                GoalGameEvent(quarter = 1, time = 450,
                    scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
                GoalGameEvent(quarter = 2, time = 321,
                    scorerTeamHome = false, scorerName = "Ein Lutscher", scorerNumber = 4),
                GameEvent(quarter = 1, time = 398),
                GameEvent(quarter = 3, time = 423),
                GoalGameEvent(quarter = 2, time = 345,
                    scorerTeamHome = true, scorerName = "J. Enwenaaaaaa", scorerNumber = 11)
            )
        )
    }
}
