package com.example.waterpoloresults.ui.compose.league

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.TableInfo
import commons.Game
import commons.GameResult
import commons.League
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun LeagueScreen(
    leagues: List<League>,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0
) {
    val tabTitles = listOf("Games", "Table")

    val tabPages: List<@Composable () -> Unit> = listOf(
        {
            LeagueGamesList(leagues, onGameClick = onGameClick)
        },
        { TablesList(leagues = leagues) }
    )
    LeagueScreen(tabTitles = tabTitles, tabPages = tabPages, initialTabIndex = initialTabIndex, modifier = modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeagueScreen(
    tabTitles: List<String>,
    tabPages: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0
) {
    val state = rememberPagerState(initialPage = initialTabIndex) {
        min(tabTitles.size, tabPages.size)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FancyTabRow(
            state = state,
            tabTitles = tabTitles.subList(0, state.pageCount),
            modifier = Modifier.padding(8.dp)
        )
        HorizontalPager(state = state, modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
            tabPages[it]()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FancyTabRow(
    state: PagerState,
    tabTitles: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        val coroutineScope = rememberCoroutineScope()
        tabTitles.forEachIndexed { index, tab ->
            FancyTab(
                selected = state.currentPage == index,
                title = tab,
                onClick = { coroutineScope.launch { state.animateScrollToPage(index) } },
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
fun FancyTab(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable {onClick()}) {
        Text(
            text = title,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = if (selected) 16.sp
            else 12.sp,
            fontWeight = if (selected) FontWeight.Bold
            else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 690)
@Preview(showBackground = true, widthDp = 320, heightDp = 690,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LeaguePagesPreview() {
    val dummyGames = listOf(
        Game(
            home = "Hamburger Turnerbund v. 1862",
            away = "SV Poseidon Hamburg",
            date = 1700002003000,
            result = GameResult(homeScore = arrayOf(2, 3, 1, 5), awayScore = arrayOf(0, 1, 2, 2), finished = true)
        ),
        Game(
            home = "SV Poseidon Hamburg",
            away = "Hamburger Turnerbund v. 1862",
            date = 1710002003000,
            result = GameResult(finished = false)
        )
    )

    val dummyLeagues = listOf(
        League(
            games = dummyGames
        ),
        League(
            games = dummyGames
        )
    )

    WaterpoloResultsTheme {
        Surface {
            LeagueScreen(
                leagues = dummyLeagues,
                onGameClick = {}
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun FancyTabRowPreview() {
    val state = rememberPagerState(initialPage = 0) {
        3
    }
    val tabTitles = listOf("Standings", "Results", "Schedule")

    WaterpoloResultsTheme {
        Surface {
            FancyTabRow(state, tabTitles)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FancyTabPreview() {
    WaterpoloResultsTheme {
        Surface {
            FancyTab(title = "Standings", onClick = {}, selected = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FancyTabPreviewSelected() {
    WaterpoloResultsTheme {
        Surface {
            FancyTab(title = "Results", onClick = {}, selected = true)
        }
    }
}