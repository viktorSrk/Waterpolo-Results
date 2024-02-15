package com.example.waterpoloresults.temp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.game.GameScreen
import commons.Game
import commons.GameResult
import commons.TeamSheet
import commons.gameevents.GameEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameResultActivity : ComponentActivity() {

    companion object {
        val sut = MainActivity.sut
    }

    private val game = mutableStateOf(Game())
    private val gameResult = mutableStateOf(GameResult())
    private val gameEvents = mutableStateOf(emptyList<GameEvent>())
    private val teamSheets = mutableStateOf(emptyList<TeamSheet>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getLongExtra("gameId", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedGame = sut.getGameById(gameId)
            game.value = fetchedGame

            val fetchedResult = sut.getGameResultByGameId(gameId)
            gameResult.value = fetchedResult

            val fetchedEvents = sut.getGameEventsByGameResultId(fetchedResult.id)
            gameEvents.value = fetchedEvents

            val fetchedTeamSheets = sut.getTeamSheetsByGameId(gameId)
            teamSheets.value = fetchedTeamSheets
        }

        setContent {
            GameScreen(game.value, gameResult.value, gameEvents.value, teamSheets.value)
        }
    }
}


