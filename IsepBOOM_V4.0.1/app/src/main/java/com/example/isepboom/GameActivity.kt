package com.example.isepboom



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView


import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*

import kotlinx.coroutines.*
import java.util.concurrent.Executors


var sendText = ""
var receivedText = ""
var deplacementBombe = ""
var rnds = 0
var nbj = ""
var numj = ""
var forSendText = ""

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val text = findViewById<TextView>(R.id.textViewNBJ)
        val text1 = findViewById<TextView>(R.id.textViewNUMJ)
        val text2 = findViewById<TextView>(R.id.textViewPerdu)
        val button = findViewById<Button>(R.id.buttonSTART)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val explosion = findViewById<ImageView>(R.id.explosion)
        val back = findViewById<Button>(R.id.btn_quitter)
        button1.visibility = View.GONE
        button2.visibility = View.GONE
        button3.visibility = View.GONE
        button4.visibility = View.GONE
        explosion.visibility = View.GONE
        text2.visibility = View.GONE
        button.setOnClickListener {
            sendText = "start"
            forSendText = "ok"
        }
        fun joueurCommence(): Int {
            val a = nbj.toInt()
            rnds = (1..a).random()
            return rnds
        }
        fun gameStart(){
            button.visibility = View.GONE
            findViewById<ImageView>(R.id.imageViewBOMBE).visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            text1.visibility = View.VISIBLE
            explosion.visibility = View.GONE
            joueurCommence()
            sendText = "1_$rnds"
            forSendText = "ok"
        }
        fun gameStop(){
            
            finish()
        }
        back.setOnClickListener {
            sendText = "close"
            forSendText = "ok"
            gameStop()
        }

        fun btnJoueur(){
            Thread.sleep(1000)
            when(nbj){
                "1" -> button1.visibility = View.VISIBLE
                "2" -> {
                    button1.visibility = View.VISIBLE
                    button2.visibility = View.VISIBLE
                }
                "3"-> {
                    button1.visibility = View.VISIBLE
                    button2.visibility = View.VISIBLE
                    button3.visibility = View.VISIBLE
                }
                "4"-> {
                    button1.visibility = View.VISIBLE
                    button2.visibility = View.VISIBLE
                    button3.visibility = View.VISIBLE
                    button4.visibility = View.VISIBLE
                }
            }
        }

        fun getBomb(){

            when (receivedText) {
                "1_$numj" -> {
                    btnJoueur()
                }
                "2_$numj" -> {
                    btnJoueur()
                }
                "3_$numj" -> {
                    btnJoueur()
                }
                "4_$numj" -> {
                    btnJoueur()
                }
                else -> {
                    button1.visibility = View.GONE
                    button2.visibility = View.GONE
                    button3.visibility = View.GONE
                    button4.visibility = View.GONE
                }
            }
        }

        fun bombExplosion() {
            Thread.sleep(10)
            text2.visibility = View.VISIBLE
            findViewById<ImageView>(R.id.imageViewBOMBE).visibility = View.GONE
            text.visibility = View.GONE
            text1.visibility = View.GONE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.GONE
            button4.visibility = View.GONE
            explosion.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
        }
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(200)
                        runOnUiThread {
                            text.text = ("Il y a $nbj joueurs")
                            text1.text = ("Vous êtes le joueur $numj")
                            if (receivedText == "start") gameStart()
                            whenBombe()
                            getBomb()
                            if (receivedText == "stop") bombExplosion()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()
        fun sendBombe(X: Int) {
            deplacementBombe = numj + "_$X"
            sendText = deplacementBombe
            forSendText = "ok"
        }
        button1.setOnClickListener {
            sendBombe(1)
        }
        button2.setOnClickListener {
            sendBombe(2)
        }
        button3.setOnClickListener {
            sendBombe(3)
        }
        button4.setOnClickListener {
            sendBombe(4)
        }
        Executors.newSingleThreadExecutor().execute(Runnable {
            main()
        })
    }
    private suspend fun DefaultClientWebSocketSession.outputMessages() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                receivedText = message.readText()
                println(receivedText)
                joueurPerdu()
                when (receivedText){
                    "1" -> {
                        numj = "1"
                        sendText = "onest1"
                        forSendText = "ok"
                    }
                    "2" -> {
                        numj = "2"
                        sendText = "onest2"
                        forSendText = "ok"
                    }
                    "3" -> {
                        numj = "3"
                        sendText = "onest3"
                        forSendText = "ok"
                    }
                    "4" -> {
                        numj = "4"
                        sendText = "onest4"
                        forSendText = "ok"
                    }
                    "onest1" -> nbj = "1"
                    "onest2" -> nbj = "2"
                    "onest3" -> nbj = "3"
                    "onest4" -> nbj = "4"
                }
            }
        } catch (e: Exception) {
            println("Error pendant la récéption: " + e.localizedMessage)
        }
    }
    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {

            if (sendText.equals("exit", true)) return
            try {
                if (forSendText == "ok") {
                    send(sendText)
                    sendText = ""
                    forSendText= ""
                }
            } catch (e: Exception) {
                println("Ereur lors de l'envoi:  " + e.localizedMessage)
                return
            }
        }
    }
    private fun main() {
        val client = HttpClient {
            install(WebSockets)
        }
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "myserver.fr", port = 8000, path = "/tictactoe") {
                val messageOutputRoutine = launch { outputMessages() }
                val userInputRoutine = launch { inputMessages() }
                userInputRoutine.join()
                messageOutputRoutine.cancelAndJoin()
            }
        }
        client.close()
        println("La connection est terminée. Au revoir")
    }
    private fun moveToPlayer(button: Button){
        val newX = button.x
        val newY = button.y
        val bombe = findViewById<ImageView>(R.id.imageViewBOMBE)
        bombe.x = newX - 150
        bombe.y = newY - 500
    }

    private fun joueurPerdu(){
        when(receivedText){
            "1_1" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 1 a perdu")
            "2_1" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 1 a perdu")
            "3_1" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 1 a perdu")
            "4_1" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 1 a perdu")

            "1_2" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 2 a perdu")
            "2_2" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 2 a perdu")
            "3_2" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 2 a perdu")
            "4_2" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 2 a perdu")

            "1_3" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 3 a perdu")
            "2_3" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 3 a perdu")
            "3_3" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 3 a perdu")
            "4_3" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 3 a perdu")

            "1_4" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 4 a perdu")
            "2_4" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 4 a perdu")
            "3_4" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 4 a perdu")
            "4_4" -> findViewById<TextView>(R.id.textViewPerdu).text = ("Le joueur 4 a perdu")
        }
    }
    fun whenBombe(){
        when(receivedText){
            "1_1" -> moveToPlayer(findViewById(R.id.button1))
            "1_2" -> moveToPlayer(findViewById(R.id.button2))
            "1_3" -> moveToPlayer(findViewById(R.id.button3))
            "1_4" -> moveToPlayer(findViewById(R.id.button4))
            "2_1" -> moveToPlayer(findViewById(R.id.button1))
            "2_2" -> moveToPlayer(findViewById(R.id.button2))
            "2_3" -> moveToPlayer(findViewById(R.id.button3))
            "2_4" -> moveToPlayer(findViewById(R.id.button4))

            "3_1" -> moveToPlayer(findViewById(R.id.button1))
            "3_2" -> moveToPlayer(findViewById(R.id.button2))
            "3_3" -> moveToPlayer(findViewById(R.id.button3))
            "3_4" -> moveToPlayer(findViewById(R.id.button4))

            "4_1" -> moveToPlayer(findViewById(R.id.button1))
            "4_2" -> moveToPlayer(findViewById(R.id.button2))
            "4_3" -> moveToPlayer(findViewById(R.id.button3))
            "4_4" -> moveToPlayer(findViewById(R.id.button4))
        }
    }
}