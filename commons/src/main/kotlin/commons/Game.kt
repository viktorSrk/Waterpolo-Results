package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val home: String = "",
    val away: String = "",

    @JsonBackReference
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "league_id", referencedColumnName = "id")
    var league: League? = null
) {

}