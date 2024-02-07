package commons

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

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
    val games: List<Game> = emptyList(),

    @JsonManagedReference
    @OneToOne(mappedBy = "league", orphanRemoval = true, cascade = [CascadeType.ALL])
    val dsvInfo: LeagueDsvInfo? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as League

        if (name != other.name) return false
        if (country != other.country) return false
        if (region != other.region) return false
        return dsvInfo == other.dsvInfo
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + (dsvInfo?.hashCode() ?: 0)
        return result
    }
}