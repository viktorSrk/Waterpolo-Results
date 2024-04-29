package com.example.waterpoloresults

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.waterpoloresults.ui.theme.WaterpoloResultsTheme
import com.example.waterpoloresults.utils.ServerUtils
import commons.League
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val SERVER_URL = "192.168.0.169:8080"
        val sut = ServerUtils(SERVER_URL)

        val leagues = mutableStateOf(listOf<League>())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            leagues.value = sut.getLeagues()
        }

        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val leaguesState by remember { MainActivity.leagues }

    WaterpoloResultsTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {}
        ) { innerPadding ->
            MyNavHost(
                navController = navController,
                leaguesState = leaguesState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 720)
@Preview(showBackground = true, widthDp = 380, heightDp = 720,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyAppPreview() {
    MyApp()
}