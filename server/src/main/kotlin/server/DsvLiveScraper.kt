package server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import commons.Game
import commons.GameDsvInfo
import commons.GameResult
import commons.League
import commons.LeagueDsvInfo
import commons.gameevents.GameEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import server.api.GameController

class DsvLiveScraper(private val websiteUrl: String, val gameController: GameController? = null) {

    fun getLiveGames(): List<Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Pair<String, String>>> {
        val client = OkHttpClient()

        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = "data=%7B%22H%22%3A%22wbhub%22%2C%22M%22%3A%22getAllGames%22%2C%22A%22%3A%5B%5D%2C%22I%22%3A0%7D".toRequestBody(mediaType)

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
        val pairs: MutableList<Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Pair<String, String>>> = ArrayList()
        gamesResponses.forEach {
            pairs.add(parseGame(it))
        }

        return pairs.toList()
    }

    private fun parseGame(response: LinkedHashMap<String, Any>): Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Pair<String, String>> {
        val leagueDsvInfo = LeagueDsvInfo(
            dsvLeagueSeason = response.get("Season") as Int,
            dsvLeagueId = response.get("LeagueID") as Int,
            dsvLeagueGroup = response.get("Gruppe") as String,
            dsvLeagueKind = response.get("LeagueKind") as String
        )
        val gameDsvInfo = GameDsvInfo(
            dsvGameId = response.get("GameID") as Int
        )
        val homeRegId = response.get("HomeRegID") as String
        val guestRegId = response.get("GuestRegID") as String
        return Pair(Pair(leagueDsvInfo, gameDsvInfo), Pair(homeRegId, guestRegId))
    }

    fun getGameEvents(leagueDsvInfo: LeagueDsvInfo, gameDsvInfo: GameDsvInfo, homeRegId: String, guesRegId: String): List<GameEvent> {
        val client = OkHttpClient()

        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = ("data=%7B%22H%22%3A%22wbhub%22%2C%22M%22%3A%22getGamePlan%22%2C%22A%22%3A" +
                "%5B" +
                    "%22${leagueDsvInfo.dsvLeagueSeason}%22%2C" +
                    "%22${leagueDsvInfo.dsvLeagueId}%22%2C" +
                    "%22${leagueDsvInfo.dsvLeagueGroup}%22%2C" +
                    "%22${leagueDsvInfo.dsvLeagueKind}%22%2C" +
                    "%22${gameDsvInfo.dsvGameId}%22%2C" +
                    "%22$homeRegId%22%2C" +
                    "%221%22%2C" +
                    "%22$guesRegId%22%2C" +
                    "%221%22" +
                "%5D" +
                "%2C%22I%22%3A1%7D").toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$websiteUrl/signalr/send?transport=serverSentEvents&clientProtocol=2.1&connectionToken=11EgBY2M3cfYaVrj2dV41p9hIP10WHKHfT1GZZ6ys2cUoFENnezcA3hZ9cMoOLG4Tn0uM%2F5G5mKCQzQTF5WEVlB6taaCvRNVOYwExOy6wVenGc70QOdjoi2BrZD4IfYa&connectionData=%5B%7B%22name%22%3A%22wbhub%22%7D%5D HTTP/2")
            .post(body)
            .build()

        val responseObject: LinkedHashMap<String, Any>

        client.newCall(request).execute().use {response ->
            val responseJson = response.body?.string()
            val mapper = jacksonObjectMapper()
            responseObject = mapper.readValue(responseJson ?: "")
        }

        val gameEventsResponse: ArrayList<LinkedHashMap<String, Any>> = responseObject.get("R") as ArrayList<LinkedHashMap<String, Any>>

        // TODO: Parse game events
        return emptyList()
    }

}