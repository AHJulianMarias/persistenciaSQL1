package com.example.persistenciasql1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SecondaryActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragments_layout)
        val buttonMapa = findViewById<Button>(R.id.botonMapa)
        val buttonVolver = findViewById<Button>(R.id.botonVolver)
        val buttonCalculo = findViewById<Button>(R.id.botonCalculos)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MapFragment())
            .commit()

        buttonCalculo.setOnClickListener{
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer,CalculoFragment())
                .commit()

        }

        buttonMapa.setOnClickListener{
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer,MapFragment())
                .commit()

        }

        buttonVolver.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }



}