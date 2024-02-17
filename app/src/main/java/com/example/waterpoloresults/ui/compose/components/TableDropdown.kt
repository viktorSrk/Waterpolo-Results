package com.example.waterpoloresults.ui.compose.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.utils.determineLeagueKind
import commons.League

@Composable
fun TableDropdown(
    tablesIndexState: MutableState<String>,
    tables: Map<String, List<League>>,
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
                text = { Text("All Tables", modifier = Modifier.weight(1f)) },
                onClick = {
                    tablesIndexState.value = "All"
                    expanded = false
                }
            )
            tables.forEach { (kind, leagues) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = kind,
                            modifier = Modifier.weight(1f)
                        )
                    },
                    onClick = {
                        tablesIndexState.value = kind
                        expanded = false
                    }
                )
            }
        }
    }
}
