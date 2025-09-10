package com.finantrack.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent

fun Application.configureRouting() {
    routing {
        // Esta rota responde à URL principal do site ("/")
        get("/") {
            // ThymeleafContent diz ao Ktor para renderizar o template "index.html"
            // O mapOf() vazio é para passar dados para a página (veremos depois)
            call.respond(ThymeleafContent("index.html", emptyMap()))
        }
    }
}