package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.determineLeagueKind
import commons.League
import commons.LeagueDsvInfo

@Composable
fun GamesDropdown(
    gamesIndexState: MutableState<Int>,
    groups: List<League>,
    buttonString: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(8.dp)) {
        var expanded by remember { mutableStateOf(false) }
        OutlinedButton(onClick = { expanded = !expanded }) {
            Text(text = buttonString, modifier = Modifier.weight(1f))
            Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft, contentDescription = "")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
            DropdownMenuItem(
                text = { Text("All Games") },
                onClick = {
                    gamesIndexState.value = -1
                    expanded = false
                }
            )
            groups.forEachIndexed { index, group ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Text(
                                "Group " + (group.dsvInfo?.dsvLeagueGroup ?: "?"),
                                modifier = Modifier.weight(1f)
                            )
                            Text(determineLeagueKind(group), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    onClick = {
                        gamesIndexState.value = index
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GamesDropdownPreview() {
    val gamesIndex = remember { mutableStateOf(0) }
    val groups = listOf(
        League(dsvInfo = LeagueDsvInfo(
            dsvLeagueGroup = "A",
            dsvLeagueKind = "P"
        )),
        League(dsvInfo = LeagueDsvInfo(
            dsvLeagueGroup = "B",
            dsvLeagueKind = "V"
        )),
        League(dsvInfo = LeagueDsvInfo(
            dsvLeagueGroup = "C",
            dsvLeagueKind = "Z"
        ))
    )

    WaterpoloResultsTheme {
        Surface {
            GamesDropdown(gamesIndexState = gamesIndex, groups = groups, buttonString = "All Games")
        }
    }
}
