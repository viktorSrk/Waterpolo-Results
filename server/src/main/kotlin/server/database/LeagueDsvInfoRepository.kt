package server.database

import commons.LeagueDsvInfo
import org.springframework.data.jpa.repository.JpaRepository

interface LeagueDsvInfoRepository : JpaRepository<LeagueDsvInfo, Long>