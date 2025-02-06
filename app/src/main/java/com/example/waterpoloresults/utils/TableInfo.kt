package com.example.waterpoloresults.utils

import commons.Game

data class TableInfo(
    val positions: Map<String, Int> = emptyMap(),
    val mp: Map<String, Int> = emptyMap(),
    val pts: Map<String, Int> = emptyMap(),
    val dif: Map<String, Int> = emptyMap()
)  {
    companion object {
        fun createTable(games: List<Game>): TableInfo {
            val positions = mutableMapOf<String, Int>()
            val mp = mutableMapOf<String, Int>()
            val pts = mutableMapOf<String, Int>()
            val dif = mutableMapOf<String, Int>()

            games.forEach {
                positions[it.home] = 0
                positions[it.away] = 0
            }

            games.filter{ it.result != null && it.result!!.finished }.forEach { game ->
                val homeTeam = game.home
                val awayTeam = game.away

                mp[homeTeam] = (mp[homeTeam]?: 0) + 1
                mp[awayTeam] = (mp[awayTeam]?: 0) + 1

                val homeScore = game.result!!.homeScore.sum()
                val awayScore = game.result!!.awayScore.sum()

                if (homeScore > awayScore) {
                    pts[homeTeam] = (pts[homeTeam]?: 0) + 2
                } else if (homeScore < awayScore) {
                    pts[awayTeam] = (pts[awayTeam]?: 0) + 2
                } else {
                    pts[homeTeam] = (pts[homeTeam]?: 0) + 1
                    pts[awayTeam] = (pts[awayTeam]?: 0) + 1
                }

                dif[homeTeam] = (dif[homeTeam]?: 0) + (homeScore - awayScore)
                dif[awayTeam] = (dif[awayTeam]?: 0) + (awayScore - homeScore)
            }

            positions.putAll(calculatePositions(positions.keys.toList(), pts, dif))

            return TableInfo(positions, mp, pts, dif)
        }

        private fun calculatePositions(teams: List<String>, pts: Map<String, Int>, dif: Map<String, Int>): Map<String, Int> {
            val sortedTeams = teams.sortedByDescending { dif[it]?: 0 }.sortedByDescending { pts[it]?: 0 }
            val positions = mutableMapOf<String, Int>()
            sortedTeams.forEachIndexed { index, team ->
                if (index != 0 && pts[sortedTeams[index - 1]] == pts[team] && dif[sortedTeams[index - 1]] == dif[team]) {
                    positions[team] = positions[sortedTeams[index - 1]]!!
                } else {
                    positions[team] = index + 1
                }
            }
            return positions
        }
    }
}