package com.puzzle.backend

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.string
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest
    @Autowired
    constructor(
        val mockMvc: MockMvc,
    ) {
        @Test
        fun `GET success should return Hello World!`() {
            mockMvc
                .get("/api/v1/test/success")
                .andExpect {
                    status { isOk() }
                    content { string("Hello World!") }
                }
        }
    }
