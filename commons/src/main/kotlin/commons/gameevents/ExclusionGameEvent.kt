package commons.gameevents

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("EXCLUSION")
class ExclusionGameEvent(
    time: Long = 0,
    quarter: Int = 0,

    val excludedName: String = "",
    val excludedNumber: Int = 0,
    val excludedTeamHome: Boolean = true
) : GameEvent(time = time, quarter = quarter) {
}