package com.example.waterpoloresults.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import commons.Game
import commons.GameResult
import commons.League
import commons.TeamSheet
import commons.gameevents.GameEvent
import okhttp3.OkHttpClient
import okhttp3.Request

class ServerUtils(
    serverUrl: String
) {
    var serverUrl: String = serverUrl
        set(value) {
            field = value
            httpUrl = "http://$field"
        }
    var httpUrl: String = "http://$serverUrl"

    private val client = OkHttpClient()

    fun getLeagues(): List<League> {
        val request = Request.Builder()
            .url("$httpUrl/api/leagues")
            .get()
            .build()

        client.newCall(request).execute().use {response ->
            val leaguesJson = response.body?.string()
            val mapper = jacksonObjectMapper()
            return mapper.readValue(leaguesJson ?: "")
        }
    }
}