package com.example.waterpoloresults.ui.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import commons.League

@Composable
fun LeagueEntry(
    league: League,
    modifier: Modifier = Modifier,
    onClick: (League) -> Unit = {},
    onFavoriteClick: () -> Unit = {}) {

    Divider()
    ListItem(modifier = modifier
        .fillMaxWidth()
        .clickable { onClick(league) },
        headlineContent = { Text(text = league.name) },
        trailingContent = {
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "")
                },
                onClick = onFavoriteClick
            )
        }
    )
}

