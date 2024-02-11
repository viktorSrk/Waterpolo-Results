package server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplicationNoScrape

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

