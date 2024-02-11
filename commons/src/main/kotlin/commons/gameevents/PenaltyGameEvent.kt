package commons.gameevents

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("PENALTY")
class PenaltyGameEvent(
    time: Long = 0,
    quarter: Int = 0,

    val penalizedName: String = "",
    val penalizedNumber: Int = 0,
    val penalizedTeamHome: Boolean = true
) : GameEvent(time = time, quarter = quarter) {
}