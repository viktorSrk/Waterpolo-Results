package commons

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
data class LeagueDsvInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "league_id", referencedColumnName = "id")
    var league: League? = null,

    val dsvLeagueSeason: Int = -1,
    val dsvLeagueId: Int = -1,
    val dsvLeagueGroup: String = "",
    val dsvLeagueKind: String = ""
) {

    fun buildLeagLink(): String {
        return "League.aspx?Season=" + dsvLeagueSeason +
                "&LeagueID=" + dsvLeagueId +
                "&Group=" + dsvLeagueGroup +
                "&LeagueKind=" + dsvLeagueKind
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LeagueDsvInfo

        if (dsvLeagueSeason != other.dsvLeagueSeason) return false
        if (dsvLeagueId != other.dsvLeagueId) return false
        if (dsvLeagueGroup != other.dsvLeagueGroup) return false
        return dsvLeagueKind == other.dsvLeagueKind
    }

    override fun hashCode(): Int {
        var result = dsvLeagueSeason
        result = 31 * result + dsvLeagueId
        result = 31 * result + dsvLeagueGroup.hashCode()
        result = 31 * result + dsvLeagueKind.hashCode()
        return result
    }


}