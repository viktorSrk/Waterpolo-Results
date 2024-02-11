package server

import commons.Game
import commons.League
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import server.api.GameController
import server.api.GameEventController
import server.api.LeagueController

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplicationNoScrape

fun main(args: Array<String>) {
    val context: ApplicationContext = runApplication<ServerApplication>(*args)
}

