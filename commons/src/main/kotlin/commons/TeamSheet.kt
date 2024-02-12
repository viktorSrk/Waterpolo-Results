package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class TeamSheet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ElementCollection
    @CollectionTable(name = "player", joinColumns = [JoinColumn(name = "team_sheet_id")])
    @Column(name = "player")
    val players: List<TeamSheet.Player> = emptyList(),

    val coach: String = "",

    @JsonBackReference
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "game_result_id", referencedColumnName = "id")
    var gameResult: GameResult? = null
) {
    @Embeddable
    data class Player(
        val number: Int = 0,
        val name: String = ""
    )
}

