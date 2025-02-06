package com.example.waterpoloresults.utils

import commons.Game
import commons.League
import commons.LeagueDsvInfo
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CupsKtTest {

    @Test
    fun jokeTest() {
//        val cupSut = ServerUtils("192.168.2.165:8080").getLeagues().first()
//        val cupSutTree = determineCupTree(cupSut)
//        println(cupSutTree)

        val games = listOf<Game>(
            Game(home = "A", away = "B", date = 0),
            Game(home = "B", away = "C", date = 1),
            Game(home = "C", away = "D", date = 2)
        )
        val cup = League(games = games, dsvInfo = LeagueDsvInfo(dsvLeagueKind = "C"))
        val cupTree = determineCupTree(cup)

        val expected = mapOf(
            1 to listOf(
                Game(home = "A", away = "B", date = 0)
            ),
            2 to listOf(
                Game(home = "B", away = "C", date = 1)
            ),
            3 to listOf(
                Game(home = "C", away = "D", date = 2)
            )
        )

        assertEquals(3, cupTree.size)
        assertEquals(expected[1], cupTree[1])
        assertEquals(expected[2], cupTree[2])
        assertEquals(expected[3], cupTree[3])
    }

    @Test
    fun determineCupTree() {
        val games = listOf<Game>(
            Game(home = "A", away = "B"),
            Game(home = "B", away = "C"),
            Game(home = "C", away = "D")
        )

        val cup = League(games = games, dsvInfo = LeagueDsvInfo(dsvLeagueKind = "C"))
        val cupTree = determineCupTree(cup)

        val expected = mapOf(
            1 to listOf(
                Game(home = "A", away = "B"),
                Game(home = "C", away = "D")
            ),
            2 to listOf(
                Game(home = "B", away = "C")
            )
        )

        assertEquals(2, cupTree.size)
        assertEquals(expected[1], cupTree[1])
        assertEquals(expected[2], cupTree[2])
    }

    @Test
    fun determineAmountOfTeams() {
        val games = listOf<Game>(
            Game(home = "A", away = "B"),
            Game(home = "B", away = "C"),
            Game(home = "C", away = "D")
        )

        assertEquals(4, determineAmountOfTeams(games))
    }

    @Test
    fun noTeamsOfGameInRound() {
        val game = Game(home = "A", away = "B")
        val round = listOf(
            Game(home = "C", away = "D"),
            Game(home = "E", away = "F")
        )

        val round2 = listOf(
            Game(home = "C", away = "B"),
            Game(home = "E", away = "F")
        )

        assertEquals(true, noTeamsOfGameInRound(game, round))
        assertEquals(false, noTeamsOfGameInRound(game, round2))
    }
}