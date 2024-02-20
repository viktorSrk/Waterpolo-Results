package com.example.waterpoloresults.ui.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game

@Composable
fun CupTree(
    tree: Map<Int, List<Game>>,
    onGameClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val roundIndexState = remember { mutableStateOf(tree.keys.size) }
    val roundIndex = roundIndexState.value
    val buttonString = getRoundString(roundIndex, tree)

    Column(modifier = modifier) {
        CupDropdown(roundsIndexState = roundIndexState, tree = tree, buttonString = buttonString)
        tree[roundIndex]?.forEach { game ->
            GameCard(
                game = game,
                onClick = onGameClick,
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 570)
@Preview(showBackground = true, widthDp = 320, heightDp = 570,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CupTreePreview() {
    val dummyCupTree = mapOf(
        1 to listOf(
            Game(home = "Team1", away = "Team2"),
            Game(home = "Team3", away = "Team4"),
            Game(home = "Team5", away = "Team6"),
            Game(home = "Team7", away = "Team8"),
            Game(home = "Team9", away = "Team10"),
            Game(home = "Team11", away = "Team12"),
            Game(home = "Team13", away = "Team14"),
            Game(home = "Team15", away = "Team16"),
            Game(home = "Team17", away = "Team18"),
            Game(home = "Team19", away = "Team20"),
            Game(home = "Team21", away = "Team22"),
            Game(home = "Team23", away = "Team24"),
            Game(home = "Team25", away = "Team26"),
            Game(home = "Team27", away = "Team28"),
            Game(home = "Team29", away = "Team30"),
            Game(home = "Team31", away = "Team32"),
        ),
        2 to listOf(
            Game(home = "Team1", away = "Team3"),
            Game(home = "Team5", away = "Team7"),
            Game(home = "Team9", away = "Team11"),
            Game(home = "Team13", away = "Team15"),
            Game(home = "Team17", away = "Team19"),
            Game(home = "Team21", away = "Team23"),
            Game(home = "Team25", away = "Team27"),
            Game(home = "Team29", away = "Team31"),
        ),
        3 to listOf(
            Game(home = "Team1", away = "Team5"),
            Game(home = "Team9", away = "Team13"),
            Game(home = "Team17", away = "Team21"),
            Game(home = "Team25", away = "Team29"),
        ),
        4 to listOf(
            Game(home = "Team1", away = "Team9"),
            Game(home = "Team17", away = "Team25"),
        ),
        5 to listOf(
            Game(home = "Team1", away = "Team17"),
        ),
    )

    WaterpoloResultsTheme {
        Surface {
            CupTree(tree = dummyCupTree)
        }
    }
}
