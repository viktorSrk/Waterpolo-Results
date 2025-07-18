package commons.gameevents

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import commons.GameResult
import jakarta.persistence.CascadeType
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "event_type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = GoalGameEvent::class, name = "GOAL"),
    JsonSubTypes.Type(value = ExclusionGameEvent::class, name = "EXCLUSION"),
    JsonSubTypes.Type(value = PenaltyGameEvent::class, name = "PENALTY"),
    JsonSubTypes.Type(value = TimeoutGameEvent::class, name = "TIMEOUT")
)
open class GameEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    open val time: Long = 0,
    open val quarter: Int = 0,

    @JsonBackReference
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "game_result_id", referencedColumnName = "id")
    open var gameResult: GameResult? = null
) {

}
