package com.example.appintegrada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnEncuesta).setOnClickListener {
            val intent = Intent(this, EncuestaActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnConversionPeso).setOnClickListener {
            val intent = Intent(this, ConversionPesoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnConversionMoneda).setOnClickListener {
            val intent = Intent(this, ConversionMonedaActivity::class.java)
            startActivity(intent)
        }
    }
}