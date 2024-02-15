package com.example.waterpoloresults.ui.compose.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.compose.GameEventRow
import com.example.waterpoloresults.ui.compose.components.PeriodDivider
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent

@Composable
fun EventsSheet(
    gameEvents: List<GameEvent>,
    modifier: Modifier = Modifier
){

    val gameEventsPerQuarter = gameEvents.groupBy { it.quarter }
        .toSortedMap { a, b -> a.compareTo(b)}
        .mapValues { it.value.sortedByDescending { e -> e.time } }

    LazyColumn(modifier = modifier) {
        gameEventsPerQuarter.forEach { (period, events) ->
            item {
                PeriodDivider(period = period, modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp))
            }
            items(events) { e ->
                GameEventRow(event = e, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameResultEventsSheetPreview() {
    WaterpoloResultsTheme {
        Surface {
            EventsSheet(
                gameEvents = listOf(
                    GameEvent(quarter = 1, time = 441),
                    GoalGameEvent(
                        quarter = 2, time = 321,
                        scorerTeamHome = false, scorerName = "Ein Lutscher", scorerNumber = 4
                    ),
                    GameEvent(quarter = 1, time = 398),
                    GameEvent(quarter = 3, time = 423),
                    GoalGameEvent(
                        quarter = 2, time = 345,
                        scorerTeamHome = true, scorerName = "J. Enwenaaaaaa", scorerNumber = 11
                    ),
                    GoalGameEvent(
                        scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12
                    ),
                    ExclusionGameEvent(
                        excludedTeamHome = true, excludedName = "F. Lenger", excludedNumber = 3
                    ),
                    PenaltyGameEvent(
                        penalizedTeamHome = true, penalizedName = "N. Sommer", penalizedNumber = 2
                    ),
                    TimeoutGameEvent(
                        teamHome = true
                    )
                )
            )
        }
    }
}
