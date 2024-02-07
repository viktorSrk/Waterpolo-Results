package com.example.waterpoloresults.utils

import org.junit.Test
import org.junit.Assert.*

class ServerUtilsTest {

    @Test
    fun getServerUrl() {
    }

    @Test
    fun setServerUrl() {
        val server = ServerUtils("localhost")
        assertEquals("localhost", server.serverUrl)
        assertEquals("http://localhost", server.httpUrl)

        server.serverUrl = "localhost:8080"
        assertEquals("localhost:8080", server.serverUrl)
        assertEquals("http://localhost:8080", server.httpUrl)
    }

    @Test
    fun getHttpUrl() {
    }

    @Test
    fun setHttpUrl() {
    }
}