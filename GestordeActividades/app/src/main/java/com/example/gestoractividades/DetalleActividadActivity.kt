package com.example.gestoractividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DetalleActividadActivity : AppCompatActivity() {

    private lateinit var actividad: Actividad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_actividad)

        // Obtener la actividad del Intent
        actividad = intent.getSerializableExtra("actividad") as Actividad

        // Referencias a las vistas
        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val tvHora = findViewById<TextView>(R.id.tvHora)
        val tvUbicacion = findViewById<TextView>(R.id.tvUbicacion)
        val tvNotificacion = findViewById<TextView>(R.id.tvNotificacion)

        val btnMaps = findViewById<Button>(R.id.btnMaps)
        val btnCalendario = findViewById<Button>(R.id.btnCalendario)
        val btnCompartir = findViewById<Button>(R.id.btnCompartir)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Mostrar los datos de la actividad
        tvTitulo.text = actividad.titulo
        tvDescripcion.text = actividad.descripcion
        tvFecha.text = actividad.fecha
        tvHora.text = actividad.hora
        tvUbicacion.text = actividad.ubicacion
        tvNotificacion.text = if (actividad.notificacion) "Sí" else "No"

        // Botón para abrir Google Maps
        btnMaps.setOnClickListener {
            if (actividad.ubicacion.isNotEmpty()) {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(actividad.ubicacion)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // Si Google Maps no está instalado, usar intent genérico
                    val webIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://maps.google.com/?q=${Uri.encode(actividad.ubicacion)}"))
                    startActivity(webIntent)
                }
            } else {
                Toast.makeText(this, "No hay ubicación especificada", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para agregar al calendario - VERSIÓN SIMPLE QUE FUNCIONA
        btnCalendario.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_INSERT)
                intent.data = CalendarContract.Events.CONTENT_URI
                intent.putExtra(CalendarContract.Events.TITLE, actividad.titulo)
                intent.putExtra(CalendarContract.Events.DESCRIPTION, actividad.descripcion)
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, actividad.ubicacion)

                // SOLO arreglar la fecha/hora - VERSIÓN SIMPLE
                try {
                    val fechaHoraString = "${actividad.fecha} ${actividad.hora}"
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val date = formatter.parse(fechaHoraString)

                    if (date != null) {
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time + (60 * 60 * 1000)) // +1 hora
                    }
                } catch (e: Exception) {
                    // Si hay error con fecha, continuar sin timestamp (como antes)
                }

                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir el calendario", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para compartir
        btnCompartir.setOnClickListener {
            val textoCompartir = """
                📅 ${actividad.titulo}
                
                📝 Descripción: ${actividad.descripcion}
                📆 Fecha: ${actividad.fecha}
                ⏰ Hora: ${actividad.hora}
                📍 Ubicación: ${actividad.ubicacion}
                🔔 Notificación: ${if (actividad.notificacion) "Sí" else "No"}
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, textoCompartir)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Actividad: ${actividad.titulo}")

            startActivity(Intent.createChooser(intent, "Compartir actividad"))
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}