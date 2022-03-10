package com.example.isepboom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.buttonGOTOGAME)
        button.setOnClickListener {
            openGameActivity()
        }
        val button1 = findViewById<Button>(R.id.buttonQUIT)
        button1.setOnClickListener{
            exitProcess()
        }
    }
    fun openGameActivity(){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)}

    fun exitProcess() {
        System.exit(-1)
    }
}