package com.puzzle.websocket.temp

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TempController {
    @GetMapping("", "/")
    fun goIndex():String {
        println("들어옴")
        return "index.html"
    }
}