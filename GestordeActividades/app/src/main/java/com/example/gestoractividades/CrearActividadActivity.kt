package com.example.gestoractividades

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CrearActividadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_actividad)

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etHora = findViewById<EditText>(R.id.etHora)
        val etUbicacion = findViewById<EditText>(R.id.etUbicacion)
        val cbNotificacion = findViewById<CheckBox>(R.id.cbNotificacion)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val hora = etHora.text.toString().trim()
            val ubicacion = etUbicacion.text.toString().trim()
            val notificacion = cbNotificacion.isChecked

            // Validaciones básicas
            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fecha.isEmpty()) {
                Toast.makeText(this, "La fecha es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (hora.isEmpty()) {
                Toast.makeText(this, "La hora es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear nueva actividad
            val nuevaActividad = Actividad(titulo, descripcion, fecha, hora, ubicacion, notificacion)

            // Agregar a la lista global
            MainActivity.listaActividades.add(nuevaActividad)

            // Ir a la pantalla de detalles
            val intent = Intent(this, DetalleActividadActivity::class.java)
            intent.putExtra("actividad", nuevaActividad)
            startActivity(intent)

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "Actividad guardada exitosamente", Toast.LENGTH_SHORT).show()

            finish() // Cerrar esta actividad
        }

        btnCancelar.setOnClickListener {
            finish() // Volver a la actividad anterior
        }
    }
}