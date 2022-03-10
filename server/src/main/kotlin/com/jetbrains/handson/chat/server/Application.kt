package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
//import jdk.internal.org.jline.utils.Colors.s
import java.util.*
import java.util.concurrent.Executors

var timer =""

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/tictac") {
            println("Ajouter un joueur !")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("${connections.count()}")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    var gameStartOrStop = receivedText
                    if (receivedText == "start"){
                        connections.forEach {
                            it.session.send(gameStartOrStop)
                        }
                        Executors.newSingleThreadExecutor().execute(Runnable {
                            startTimer()
                        })
                        if (timer == "timer"){
                        gameStartOrStop = "stop"
                        connections.forEach {
                            it.session.send(gameStartOrStop)
                        }
                        }
                    }
                    else {
                    connections.forEach {
                        it.session.send(gameStartOrStop)
                    }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Fermer $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}

fun startTimer(): String {
    val time = (5000..20000).random().toLong()
    Thread.sleep(time)
    timer = "timer"
    return timer
}

