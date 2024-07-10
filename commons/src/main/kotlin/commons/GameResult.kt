package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import commons.gameevents.GameEvent
import commons.gameevents.GoalGameEvent
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
data class GameResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var homeScore: Array<Int> = arrayOf(0, 0, 0, 0),
    var awayScore: Array<Int> = arrayOf(0, 0, 0, 0),
    var finished: Boolean = false,

    @JsonManagedReference
    @OneToMany(mappedBy = "gameResult", orphanRemoval = true, targetEntity = GameEvent::class)
    var gameEvents: List<GameEvent> = emptyList(),

    @JsonManagedReference
    @OneToMany(mappedBy = "gameResult", orphanRemoval = true)
    var teamSheets: List<TeamSheet> = emptyList(),

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    var game: Game? = null
) {

    fun calculateTeamScoreFromEvents(teamHome: Boolean = false, teamAway: Boolean = false): Array<Int> {
        if (teamHome == teamAway) {
            throw IllegalArgumentException("Both arguments (teams) cannot be the same value = $teamHome")
        }

        val score = arrayOf(0, 0, 0, 0)
        for (i in 0..gameEvents.size) {
            val it = gameEvents[i]
            if (it !is GoalGameEvent || it.scorerTeamHome != teamHome) {
                continue
            }
            score[it.quarter]++
        }

        if (teamHome) {
            homeScore = score
        } else {
            awayScore = score
        }

        return score
    }

}