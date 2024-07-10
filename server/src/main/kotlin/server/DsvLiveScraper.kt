package server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import commons.GameDsvInfo
import commons.LeagueDsvInfo
import commons.gameevents.ExclusionGameEvent
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import commons.gameevents.PenaltyGameEvent
import commons.gameevents.TimeoutGameEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import server.api.GameController

class DsvLiveScraper(private val websiteUrl: String, val gameController: GameController? = null) {

    fun getLiveGames(): List<Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Array<Any>>> {
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
        val pairs: MutableList<Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Array<Any>>> = ArrayList()
        gamesResponses.forEach {
            pairs.add(parseGame(it))
        }

        return pairs.toList()
    }

    private fun parseGame(response: LinkedHashMap<String, Any>): Pair<Pair<LeagueDsvInfo, GameDsvInfo>, Array<Any>> {
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
        val homeTeamId = response.get("HomeTeamID") as Int
        val guestRegId = response.get("GuestRegID") as String
        val guestTeamId = response.get("GuestTeamID") as Int
        return Pair(Pair(leagueDsvInfo, gameDsvInfo), arrayOf(homeRegId, homeTeamId, guestRegId, guestTeamId))
    }

    fun getGameEvents(leagueDsvInfo: LeagueDsvInfo, gameDsvInfo: GameDsvInfo, homeRegId: String, homeTeamId: Int, guesRegId: String, guestTeamId: Int): List<GameEvent> {
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
                    "%22$homeTeamId%22%2C" +
                    "%22$guesRegId%22%2C" +
                    "%22$guestTeamId%22" +
                "%5D" +
                "%2C%22I%22%3A1%7D").toRequestBody(mediaType)

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

        val gameEventsResponse: ArrayList<LinkedHashMap<String, Any>> = responseObject.get("R") as ArrayList<LinkedHashMap<String, Any>>
        val gameEvents: MutableList<GameEvent> = ArrayList()

        gameEventsResponse.forEach {
            val event = parseGameEvent(it)
            if (event != null) {
                gameEvents.add(event)
            }
        }

        return gameEvents
    }

    fun parseGameEvent(response: LinkedHashMap<String, Any>): GameEvent? {
        var timeString = response.get("EventTime") as String
        timeString = timeString.substring(timeString.length - 5, timeString.length)
        val minute = timeString.substring(0, 2).toLong()
        val second = timeString.substring(3, 5).toLong()
        val time = minute * 60 + second

        val quarter = response.get("Period") as Int

        val firstName = response.get("FirstName")
        val lastName = response.get("LastName")
        var name = "${if (firstName != null) firstName as String else "?"} ${if (lastName != null) lastName as String else "?"}"

        if (name == "? ?") {
            name = response.get("GamePlanPlayer") as String
        }

        val capHome = response.get("Cap") as Int
        val capAway = response.get("Cap2") as Int

        val number = if (capHome != 0) capHome else capAway
        val teamHome = capHome != 0

        val eventKey = response.get("EventKey") as String

        val event: GameEvent? = when (eventKey) {
            "T" -> GoalGameEvent(
                time = time,
                quarter = quarter,
                scorerName = name,
                scorerNumber = number,
                scorerTeamHome = teamHome
            )
            "A" -> ExclusionGameEvent(
                time = time,
                quarter = quarter,
                excludedName = name,
                excludedNumber = number,
                excludedTeamHome = teamHome
            )
            "F" -> ExclusionGameEvent( // AmE
                time = time,
                quarter = quarter,
                excludedName = name,
                excludedNumber = number,
                excludedTeamHome = teamHome
            )
            "S" -> PenaltyGameEvent(
                time = time,
                quarter = quarter,
                penalizedName = name,
                penalizedNumber = number,
                penalizedTeamHome = teamHome
            )
            "U" -> TimeoutGameEvent(
                time = time,
                quarter = quarter,
                teamHome = teamHome
            )
            "E" -> GameEvent(
                time = time,
                quarter = quarter
            )
            else -> null
        }

        return event
    }

}