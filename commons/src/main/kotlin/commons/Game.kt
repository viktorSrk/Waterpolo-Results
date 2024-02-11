package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val home: String = "",
    val away: String = "",
    val date: Long = 0,

    @JsonBackReference
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "league_id", referencedColumnName = "id")
    var league: League? = null,

    @JsonManagedReference
    @OneToOne(mappedBy = "game", orphanRemoval = true, cascade = [CascadeType.ALL])
    var result: GameResult? = null,

    @JsonManagedReference
    @OneToOne(mappedBy = "game", orphanRemoval = true, cascade = [CascadeType.ALL])
    val dsvInfo: GameDsvInfo? = null
)