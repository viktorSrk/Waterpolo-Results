package commons.gameevents

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("GOAL")
class GoalGameEvent(
    time: Long = 0,
    quarter: Int = 0,

    val scorerName: String = "",
    val scorerNumber: Int = 0,
    val scorerTeamHome: Boolean = true
) : GameEvent(time = time, quarter = quarter) {

}