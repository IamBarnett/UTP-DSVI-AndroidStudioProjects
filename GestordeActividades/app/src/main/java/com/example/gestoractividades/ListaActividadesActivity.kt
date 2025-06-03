package com.example.gestoractividades

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListaActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_actividades)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloLista)
        val listView = findViewById<ListView>(R.id.listViewActividades)
        val btnVolver = findViewById<Button>(R.id.btnVolverLista)

        // Obtener la lista de actividades de MainActivity
        val actividades = MainActivity.listaActividades

        tvTitulo.text = "Actividades Registradas (${actividades.size})"

        if (actividades.isEmpty()) {
            // Si no hay actividades, mostrar mensaje
            val mensajes = listOf("No hay actividades registradas", "Presiona 'Volver' y crea tu primera actividad")
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mensajes)
            listView.adapter = adapter
        } else {
            // Crear adapter para mostrar las actividades
            val actividadesTexto = actividades.map { actividad ->
                "${actividad.titulo}\n${actividad.fecha} - ${actividad.hora}"
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, actividadesTexto)
            listView.adapter = adapter

            // Configurar click en elementos de la lista
            listView.setOnItemClickListener { _, _, position, _ ->
                if (position < actividades.size) {
                    val actividadSeleccionada = actividades[position]
                    val intent = Intent(this, DetalleActividadActivity::class.java)
                    intent.putExtra("actividad", actividadSeleccionada)
                    startActivity(intent)
                }
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista cuando se regrese a esta actividad
        recreate()
    }
}