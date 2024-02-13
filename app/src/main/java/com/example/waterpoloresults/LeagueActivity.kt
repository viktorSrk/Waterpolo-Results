package com.example.waterpoloresults

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.TableCompact
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.min

class LeagueActivity : ComponentActivity() {

    companion object {
        val sut = MainActivity.sut
    }

    private val games = mutableStateOf(emptyList<Game>())
    private val tableInfo = mutableStateOf(TableInfo())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val leagueId = intent.getLongExtra("leagueId", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedGames = GamesActivity.sut.getLeagueById(leagueId).games
            games.value = fetchedGames

            tableInfo.value = TableInfo.createTable(fetchedGames)
        }

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LeaguePages(
                        tabTitles = listOf("Games", "Standings"),
                        tabPages = listOf(
                            { Games(games = games.value, onGameClick = { gameId -> openGameResultActivityForGame(gameId) })},
                            { Card(modifier = Modifier.padding(8.dp)) { TableCompact(positions = tableInfo.value.positions, mp = tableInfo.value.mp, pts = tableInfo.value.pts, dif = tableInfo.value.dif) } }
                        )
                    )
                }
            }
        }
    }

    private fun openGameResultActivityForGame(gameId: Long) {
        val intent = Intent(this@LeagueActivity, GameResultActivity::class.java).apply {
            putExtra("gameId", gameId)
        }
        startActivity(intent)
    }

    data class TableInfo(
        val positions: Map<String, Int> = emptyMap(),
        val mp: Map<String, Int> = emptyMap(),
        val pts: Map<String, Int> = emptyMap(),
        val dif: Map<String, Int> = emptyMap()
    )  {
        companion object {
            fun createTable(games: List<Game>): TableInfo {
                val positions = mutableMapOf<String, Int>()
                val mp = mutableMapOf<String, Int>()
                val pts = mutableMapOf<String, Int>()
                val dif = mutableMapOf<String, Int>()

                games.forEach {
                    positions[it.home] = 0
                    positions[it.away] = 0
                }

                games.filter{ it.result != null && it.result!!.finished }.forEach { game ->
                    val homeTeam = game.home
                    val awayTeam = game.away

                    mp[homeTeam] = (mp[homeTeam]?: 0) + 1
                    mp[awayTeam] = (mp[awayTeam]?: 0) + 1

                    val homeScore = game.result!!.homeScore.sum()
                    val awayScore = game.result!!.awayScore.sum()

                    if (homeScore > awayScore) {
                        pts[homeTeam] = (pts[homeTeam]?: 0) + 2
                    } else if (homeScore < awayScore) {
                        pts[awayTeam] = (pts[awayTeam]?: 0) + 2
                    } else {
                        pts[homeTeam] = (pts[homeTeam]?: 0) + 1
                        pts[awayTeam] = (pts[awayTeam]?: 0) + 1
                    }

                    dif[homeTeam] = (dif[homeTeam]?: 0) + (homeScore - awayScore)
                    dif[awayTeam] = (dif[awayTeam]?: 0) + (awayScore - homeScore)
                }

                positions.putAll(calculatePositions(positions.keys.toList(), pts, dif))

                return TableInfo(positions, mp, pts, dif)
            }

            private fun calculatePositions(teams: List<String>, pts: Map<String, Int>, dif: Map<String, Int>): Map<String, Int> {
                val sortedTeams = teams.sortedByDescending { dif[it]?: 0 }.sortedByDescending { pts[it]?: 0 }
                val positions = mutableMapOf<String, Int>()
                sortedTeams.forEachIndexed { index, team ->
                    if (index != 0 && pts[sortedTeams[index - 1]] == pts[team] && dif[sortedTeams[index - 1]] == dif[team]) {
                        positions[team] = positions[sortedTeams[index - 1]]!!
                    } else {
                        positions[team] = index + 1
                    }
                }
                return positions
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaguePages(
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

    val tableInfo = LeagueActivity.TableInfo.createTable(dummyGames)

    WaterpoloResultsTheme {
        Surface {
            LeaguePages(
                tabTitles = listOf("Games", "Table"),
                tabPages = listOf(
                    { Games(dummyGames) },
                    { TableCompact(positions = tableInfo.positions, mp = tableInfo.mp, pts = tableInfo.pts, dif = tableInfo.dif) }
                )
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
