package server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import server.api.LeagueController

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)

}
