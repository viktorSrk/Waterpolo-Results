package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commons.Game

@Composable
fun CupDropdown(
    roundsIndexState: MutableState<Int>,
    tree: Map<Int, List<Game>>,
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
            tree.keys.forEach { round ->
                val roundString = getRoundString(round, tree)

                DropdownMenuItem(
                    text = {
                        Text(
                            text = roundString,
                            modifier = Modifier.weight(1f)
                        )
                    },
                    onClick = {
                        roundsIndexState.value = round
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getRoundString(round: Int, tree: Map<Int, List<Game>>) : String {
    var result = when(tree[round]?.size ?: 0) {
        1 -> "Final"
        2 -> "Semifinals"
        4 -> "Quarterfinals"
        8 -> "Round of 16"
        else -> "Round $round"
    }

    if (round < tree.keys.size) {
        val nextRoundString = getRoundString(round + 1, tree)
        if (
            result == "Round of 16" && nextRoundString != "Quarterfinals"
            || result == "Quarterfinals" && nextRoundString != "Semifinals"
            || result == "Semifinals" && nextRoundString != "Final"
            ) {
            result = "Round $round"
        }
    }

    return result
}
