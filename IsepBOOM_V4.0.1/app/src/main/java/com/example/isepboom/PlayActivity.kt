package com.example.isepboom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val button = findViewById<Button>(R.id.buttonGOTOGAME)
        button.setOnClickListener{
            openGameActivity()
        }
    }

    fun openGameActivity(){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}