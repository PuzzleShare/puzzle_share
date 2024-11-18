package com.puzzle.backend.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/test")
class TestController : TestControllerSpec {
    @GetMapping("/success")
    override fun success(name: String): String = "Hello $name"
}
