package com.example.waterpoloresults

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.components.TableCompact
import com.example.waterpoloresults.ui.compose.league.GamesList
import com.example.waterpoloresults.ui.compose.league.LeagueScreen
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val fetchedGames = sut.getLeagueById(leagueId).games
            games.value = fetchedGames

            tableInfo.value = TableInfo.createTable(fetchedGames)
        }

        setContent {
            WaterpoloResultsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LeagueScreen(
                        tabTitles = listOf("Games", "Standings"),
                        tabPages = listOf(
                            { GamesList(games = games.value, onGameClick = { gameId -> openGameResultActivityForGame(gameId) })},
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


