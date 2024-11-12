package com.puzzle.backend.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
) {
    @Test
    fun `GET success should return Hello World!`() {
        mockMvc
            .perform(get("/api/v1/test/success"))
            .andExpect {
                it.response.contentAsString.equals("Hello World!")
            }
    }
}
