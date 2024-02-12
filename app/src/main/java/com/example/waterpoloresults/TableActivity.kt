package com.example.waterpoloresults

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.TableCompact
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TableActivity : ComponentActivity() {

    companion object {
        val sut = MainActivity.sut
    }

    private val games = mutableStateOf(emptyList<Game>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val leagueId = intent.getLongExtra("leagueId", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedGames = sut.getLeagueById(leagueId).games
            games.value = fetchedGames
        }

        setContent {
            TablesComposition(games.value)
        }
    }
}

@Composable
fun TablesComposition(games: List<Game>) {

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

    WaterpoloResultsTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    TableCompact(
                        modifier = Modifier.padding(8.dp),
                        positions = positions, mp = mp, pts = pts, dif = dif
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 570)
@Preview(showBackground = true, widthDp = 320, heightDp = 570,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TableCompositionPreview() {
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

    TablesComposition(dummyGames)
}

fun calculatePositions(teams: List<String>, pts: Map<String, Int>, dif: Map<String, Int>): Map<String, Int> {
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
