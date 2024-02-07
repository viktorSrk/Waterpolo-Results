package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
data class GameResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val homeScore: Array<Int> = arrayOf(0, 0, 0, 0),
    val awayScore: Array<Int> = arrayOf(0, 0, 0, 0),
    val finished: Boolean = false,

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    var game: Game? = null
) {
    fun toScore(): String {
        return "${homeScore.sum()} : ${awayScore.sum()}"
    }
}