package com.example.waterpoloresults

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.waterpoloresults.ui.compose.GameResultHeader
import com.example.waterpoloresults.ui.compose.GameResultOverview
import com.example.waterpoloresults.ui.compose.GameResultSheet
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import commons.Game
import commons.GameResult
import commons.League
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameResultActivity : ComponentActivity() {

    companion object {
        val sut = MainActivity.sut
    }

    private val game = mutableStateOf(Game())
    private val gameResult = mutableStateOf(GameResult())
    private val gameEvents = mutableStateOf(emptyList<GameEvent>())

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
        }

        setContent {
            GameResultComposition(game.value, gameResult.value, gameEvents.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameResultComposition(game: Game, result: GameResult, gameEvents: List<GameEvent>) {
    WaterpoloResultsTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            val scaffoldState = rememberBottomSheetScaffoldState()

            BottomSheetScaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                sheetContent = { Box(modifier = Modifier.fillMaxHeight()) {GameResultSheet(gameEvents = gameEvents)} },
                topBar = { GameResultHeader(game = game)},
                scaffoldState = scaffoldState,
                sheetPeekHeight = 150.dp,
                sheetShadowElevation = 16.dp
            ) { innerPadding ->
                GameResultOverview(
                    game = game,
                    result = result,
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 570)
@Preview(showBackground = true, widthDp = 320, heightDp = 570,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview2() {
    GameResultComposition(
        game = Game(
            home = "Hamburger TB v. 1862",
            away = "SV Poseidon",
            league = League(
                country = "DEU",
                region = "Hamburg",
                name = "Hamburger Liga")
        ),
        result = GameResult(
            homeScore = arrayOf(2, 3, 1, 5),
            awayScore = arrayOf(0, 1, 2, 2),
            finished = true
        ),
        gameEvents = listOf(
            GameEvent(quarter = 1, time = 441),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
            GoalGameEvent(quarter = 2, time = 321,
                scorerTeamHome = false, scorerName = "Ein Lutscher", scorerNumber = 4),
            GameEvent(quarter = 1, time = 398),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GameEvent(quarter = 3, time = 423),
            GoalGameEvent(quarter = 2, time = 345,
                scorerTeamHome = true, scorerName = "J. Enwenaaaaaa", scorerNumber = 11),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12),
            GoalGameEvent(quarter = 1, time = 450,
                scorerTeamHome = true, scorerName = "V. Sersik", scorerNumber = 12)
        )
    )
}
