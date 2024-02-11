package server.database

import commons.gameevents.GameEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GameEventRepository : JpaRepository<GameEvent, Long> {
    @Query("""
        SELECT e
        FROM GameEvent e
        WHERE TYPE(e) = :eventType
    """)
    fun findByEventType(@Param("eventType") eventType: Class<out GameEvent>): List<GameEvent>
}