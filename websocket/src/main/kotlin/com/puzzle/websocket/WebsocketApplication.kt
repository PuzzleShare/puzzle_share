package com.puzzle.websocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.imageio.ImageIO

@SpringBootApplication
class WebsocketApplication {
    init {
        // ImageIO 플러그인 스캔
        ImageIO.scanForPlugins()
    }
}

fun main(args: Array<String>) {
    runApplication<WebsocketApplication>(*args)
}
