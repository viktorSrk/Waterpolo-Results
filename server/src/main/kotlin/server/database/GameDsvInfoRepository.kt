package server.database

import commons.GameDsvInfo
import org.springframework.data.jpa.repository.JpaRepository

interface GameDsvInfoRepository : JpaRepository<GameDsvInfo, Long>