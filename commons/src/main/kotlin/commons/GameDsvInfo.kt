package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
data class GameDsvInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    var game: Game? = null,

    val dsvGameId: Int = -1
) {

    fun buildGameLink(): String {
        return "Game.aspx?Season=" + game!!.league!!.dsvInfo!!.dsvLeagueSeason +
                "&LeagueID=" + game!!.league!!.dsvInfo!!.dsvLeagueId +
                "&Group=" + game!!.league!!.dsvInfo!!.dsvLeagueGroup +
                "&LeagueKind=" + game!!.league!!.dsvInfo!!.dsvLeagueKind +
                "&GameID=" + dsvGameId
    }
}