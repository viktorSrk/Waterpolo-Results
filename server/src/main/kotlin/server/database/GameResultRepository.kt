package server.database

import commons.GameResult
import org.springframework.data.jpa.repository.JpaRepository

interface GameResultRepository : JpaRepository<GameResult, Long>