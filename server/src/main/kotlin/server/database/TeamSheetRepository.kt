package server.database

import commons.TeamSheet
import org.springframework.data.jpa.repository.JpaRepository

interface TeamSheetRepository : JpaRepository<TeamSheet, Long>
