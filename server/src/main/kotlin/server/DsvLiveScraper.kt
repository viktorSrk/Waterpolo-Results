package server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DsvLiveScraper(val websiteUrl: String) {
    fun getLiveGames(): List<Game> {
        val client = OkHttpClient()

        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = "data=%7B%22H%22%3A%22wbhub%22%2C%22M%22%3A%22getAllGames%22%2C%22A%22%3A%5B%5D%2C%22I%22%3A0%7D'".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$websiteUrl/signalr/send?transport=serverSentEvents&clientProtocol=2.1&connectionToken=uFf2euJVjztJkGc14eAFNFyhV9k95wdfZnf1kuQ2BMosWCrUeS23rJ0NCwa3GTU07tP1oK7SBlPEBR9dIZ5xsJXu8pkfgbDyMb9OLhcRMJL%2BujvtBpubDyFXwkj%2F%2BUJo&connectionData=%5B%7B%22name%22%3A%22wbhub%22%7D%5D HTTP/2")
            .post(body)
            .build()

        val responseObject: LinkedHashMap<String, Any>

        client.newCall(request).execute().use {response ->
            val responseJson = response.body?.string()
            val mapper = jacksonObjectMapper()
            responseObject = mapper.readValue(responseJson ?: "")
        }

        val gamesResponses: ArrayList<LinkedHashMap<String, Any>> = responseObject.get("R") as ArrayList<LinkedHashMap<String, Any>>
        val games: MutableList<Game> = ArrayList()
        gamesResponses.forEach {
            games.add(parseGame(it))
        }

        return games.toList()
    }

    private fun parseGame(response: LinkedHashMap<String, Any>): Game {
        val home = response.get("HomeClubname") as String
        val away = response.get("GuestClubname") as String
        val date: Long = 0 // TODO: Parse date
        val league: League = League(
            dsvInfo = LeagueDsvInfo(
                dsvLeagueSeason = response.get("Season") as Int,
                dsvLeagueId = response.get("LeagueID") as Int,
                dsvLeagueGroup = response.get("Gruppe") as String,
                dsvLeagueKind = response.get("LeagueKind") as String
            )
        )
        val result: GameResult? = null // TODO: Get result
        val dsvInfo = GameDsvInfo(
            dsvGameId = response.get("GameID") as Int
        )
        return Game(home = home, away = away, league = league, dsvInfo = dsvInfo)
    }
}