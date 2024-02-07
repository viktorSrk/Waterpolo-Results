package commons

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class League(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String = "",

    val country: String = "",
    val region: String = "",

    @JsonManagedReference
    @OneToMany(mappedBy = "league", orphanRemoval = true)
    val games: List<Game> = emptyList()
) {
}