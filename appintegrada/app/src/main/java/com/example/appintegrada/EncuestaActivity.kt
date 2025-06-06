package com.example.appintegrada

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EncuestaActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var radioGroupComida: RadioGroup
    private lateinit var spinnerPasatiempo: Spinner
    private lateinit var switchDeporte: Switch
    private lateinit var btnFechaNacimiento: Button
    private lateinit var textViewFecha: TextView
    private lateinit var imageViewComida: ImageView
    private lateinit var btnMensajeDivertido: Button

    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encuesta)

        supportActionBar?.title = "Encuesta de Preferencias"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inicializarComponentes()
        configurarSpinner()
        configurarBotonFecha()
        configurarBotonMensajeDivertido()
    }

    private fun inicializarComponentes() {
        editTextNombre = findViewById(R.id.editTextNombre)
        radioGroupComida = findViewById(R.id.radioGroupComida)
        spinnerPasatiempo = findViewById(R.id.spinnerPasatiempo)
        switchDeporte = findViewById(R.id.switchDeporte)
        btnFechaNacimiento = findViewById(R.id.btnFechaNacimiento)
        textViewFecha = findViewById(R.id.textViewFecha)
        imageViewComida = findViewById(R.id.imageViewComida)
        btnMensajeDivertido = findViewById(R.id.btnMensajeDivertido)
    }

    private fun configurarSpinner() {
        val pasatiempos = arrayOf("Seleccionar...", "Leer", "Deportes", "Música", "Películas", "Videojuegos", "Cocinar")
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            pasatiempos
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPasatiempo.adapter = adapter
        }
    }

    private fun configurarBotonFecha() {
        btnFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                    textViewFecha.text = fechaSeleccionada
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }
    }

    private fun configurarBotonMensajeDivertido() {
        btnMensajeDivertido.setOnClickListener {
            Toast.makeText(this, "¡Si fueras un plato, serías el plato principal!", Toast.LENGTH_LONG).show()
        }
    }

    fun mostrarResultados(view: View) {
        if (editTextNombre.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = editTextNombre.text.toString()

        var comida = "No seleccionada"
        if (radioGroupComida.checkedRadioButtonId != -1) {
            val radioButtonSeleccionado = findViewById<RadioButton>(radioGroupComida.checkedRadioButtonId)
            comida = radioButtonSeleccionado.text.toString()
        }

        val pasatiempo = spinnerPasatiempo.selectedItem.toString()
        val practicaDeporte = if (switchDeporte.isChecked) "Sí" else "No"
        val fecha = if (fechaSeleccionada.isNotEmpty()) fechaSeleccionada else "No seleccionada"

        val mensaje = "Nombre: $nombre\n" +
                "Comida favorita: $comida\n" +
                "Pasatiempo: $pasatiempo\n" +
                "Practica deporte: $practicaDeporte\n" +
                "Fecha de nacimiento: $fecha"

        AlertDialog.Builder(this)
            .setTitle("Resultados de la Encuesta")
            .setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}