package com.example.waterpoloresults.utils

import commons.Game
import commons.League

fun determineCupTree(cup: League) : Map<Int, List<Game>> {
    val leagueKind = determineLeagueKind(cup)
    if (leagueKind != LeagueKinds.CUP) {
        throw IllegalArgumentException("League is not a cup")
    }

    val sortedGames = cup.games.sortedBy { it.date }.toMutableList()
    val cupTree = mutableMapOf<Int, MutableList<Game>>()

    var currentRound = 1
    while (sortedGames.isNotEmpty()) {
        val addedTeams = mutableSetOf<String>()
        val teamsNum = determineAmountOfTeams(sortedGames)

        val remainingGames = sortedGames.toList()

        cupTree[currentRound] = mutableListOf()
        for (game in remainingGames) {
            if (addedTeams.size == teamsNum) {
                break
            }
            if (addedTeams.contains(game.home) || addedTeams.contains(game.away)) {
                continue
            }
            if (!firstGameOfTeam(game.home, game, remainingGames) || !firstGameOfTeam(game.away, game, remainingGames)) {
                continue
            }

            cupTree[currentRound]?.add(game)
            addedTeams.add(game.home)
            addedTeams.add(game.away)
            sortedGames.remove(game)
        }

        currentRound++
    }

    for (i in 1..cupTree.size) {
        val round = cupTree[i] ?: emptyList()

        if (i == cupTree.keys.size) { break }

        val gamesToMoveUp = mutableListOf<Game>()
        for (game in round) {
            if (noTeamsOfGameInRound(game, cupTree[i + 1]?.toList() ?: emptyList())) {
                gamesToMoveUp.add(game)
            }
        }
        cupTree[i]?.removeAll(gamesToMoveUp)
        cupTree[i + 1]?.addAll(gamesToMoveUp)
    }

    return cupTree
}

fun determineAmountOfTeams(games: List<Game>) : Int {
    val teams = mutableSetOf<String>()
    games.forEach {
        teams.add(it.home)
        teams.add(it.away)
    }
    return teams.size
}

fun firstGameOfTeam(team: String, game: Game, games: List<Game>) : Boolean {
    val allGamesOfTeam = games.filter { it.home == team || it.away == team }.sortedBy { it.date }
    return allGamesOfTeam.first().date == game.date
}

fun noTeamsOfGameInRound(game: Game, round: List<Game>) : Boolean {
    return !round.any { it.home == game.home || it.away == game.home || it.home == game.away || it.away == game.away }
}
