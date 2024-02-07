package com.example.waterpoloresults.utils

import commons.League
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.glassfish.jersey.client.ClientConfig

class ServerUtils(
    serverUrl: String
) {
    var serverUrl: String = serverUrl
        set(value) {
            field = value
            httpUrl = "http://$field"
        }
    var httpUrl: String = "http://$serverUrl"

    fun getLeagues(): List<League> {
        return ClientBuilder.newClient(ClientConfig())
            .target(httpUrl).path("/api/leagues")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(object : GenericType<List<League>>() {})
    }
}