package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import commons.gameevents.GameEvent
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

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    var game: Game? = null
) {
    fun toScore(): String {
        return "${homeScore.sum()} : ${awayScore.sum()}"
    }
}