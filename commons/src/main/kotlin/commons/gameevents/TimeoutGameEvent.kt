package commons.gameevents

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("TIMEOUT")
class TimeoutGameEvent(
    time: Long = 0,
    quarter: Int = 0,

    val teamHome: Boolean = true
) : GameEvent(time = time, quarter = quarter){
}