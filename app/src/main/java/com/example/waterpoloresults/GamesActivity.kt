package com.example.waterpoloresults

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.league.GamesList
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesActivity : ComponentActivity() {

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
            WaterpoloResultsTheme {
//                 A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GamesList(
                        games = games.value,
                        onGameClick = { gameId -> openGameResultActivityForGame(gameId) }
                    )
                }
            }
        }
    }

    private fun openGameResultActivityForGame(gameId: Long) {
        val intent = Intent(this@GamesActivity, GameResultActivity::class.java).apply {
            putExtra("gameId", gameId)
        }
        startActivity(intent)
    }
}

