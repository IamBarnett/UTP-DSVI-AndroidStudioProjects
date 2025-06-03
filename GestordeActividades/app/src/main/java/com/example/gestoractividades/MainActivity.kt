package com.example.gestoractividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        val listaActividades = mutableListOf<Actividad>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCrearActividad = findViewById<Button>(R.id.btnCrearActividad)
        val btnVerActividades = findViewById<Button>(R.id.btnVerActividades)
        val btnVerCalendario = findViewById<Button>(R.id.btnVerCalendario)

        btnCrearActividad.setOnClickListener {
            val intent = Intent(this, CrearActividadActivity::class.java)
            startActivity(intent)
        }

        btnVerActividades.setOnClickListener {
            val intent = Intent(this, ListaActividadesActivity::class.java)
            startActivity(intent)
        }

        btnVerCalendario.setOnClickListener {
            // Intent implícito para abrir la aplicación de calendario
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = "application/vnd.android.calendar"
            try {
                startActivity(intent)
            } catch (e: Exception) {
                // Si no hay app de calendario, abrir configuración de fecha/hora
                val settingsIntent = Intent(android.provider.Settings.ACTION_DATE_SETTINGS)
                startActivity(settingsIntent)
            }
        }
    }
}