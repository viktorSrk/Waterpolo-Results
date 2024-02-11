package server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
@EntityScan(basePackages = ["commons", "server"])
class ServerApplicationNoScrape

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

