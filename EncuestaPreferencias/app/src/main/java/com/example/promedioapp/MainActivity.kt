package com.example.promedioapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    // Declaración de variables para elementos de UI
    private lateinit var editTextNombre: EditText
    private lateinit var radioGroupComida: RadioGroup
    private lateinit var spinnerPasatiempo: Spinner
    private lateinit var switchDeporte: Switch
    private lateinit var btnFechaNacimiento: Button
    private lateinit var textViewFecha: TextView
    private lateinit var imageViewComida: ImageView
    private lateinit var btnMensajeDivertido: Button

    // Variable para guardar la fecha seleccionada
    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar elementos de UI con findViewById
        inicializarComponentes()

        // Configurar spinner con adaptador
        configurarSpinner()

        // Configurar botón de fecha
        configurarBotonFecha()

        // Configurar evento del botón de mensaje divertido
        configurarBotonMensajeDivertido()

        // Comentamos la configuración de cambio de imagen temporalmente
        // configurarCambioImagen()
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
        // Crear adaptador para el spinner con el array de pasatiempos
        ArrayAdapter.createFromResource(
            this,
            R.array.pasatiempos_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout para el menú desplegable
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
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

    /* Comentamos esta función por ahora para evitar errores si no existen las imágenes
    private fun configurarCambioImagen() {
        radioGroupComida.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioItaliana -> imageViewComida.setImageResource(R.drawable.comida_italiana)
                R.id.radioChina -> imageViewComida.setImageResource(R.drawable.comida_china)
                R.id.radioPanamena -> imageViewComida.setImageResource(R.drawable.comida_panamena)
            }
        }
    }
    */

    private fun configurarBotonMensajeDivertido() {
        // Implementar evento con clase interna anónima
        btnMensajeDivertido.setOnClickListener {
            Toast.makeText(this, "¡Si fueras un plato, serías el plato principal!", Toast.LENGTH_LONG).show()
        }
    }

    // Función para el botón "Mostrar Resultados" (evento declarado en XML con onClick)
    fun mostrarResultados(view: View) {
        // Validar que se haya ingresado un nombre
        if (editTextNombre.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su nombre", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener los datos ingresados
        val nombre = editTextNombre.text.toString()

        var comida = "No seleccionada"
        if (radioGroupComida.checkedRadioButtonId != -1) {
            val radioButtonSeleccionado = findViewById<RadioButton>(radioGroupComida.checkedRadioButtonId)
            comida = radioButtonSeleccionado.text.toString()
        }

        val pasatiempo = spinnerPasatiempo.selectedItem.toString()

        val practicaDeporte = if (switchDeporte.isChecked) "Sí" else "No"

        val fecha = if (fechaSeleccionada.isNotEmpty())
            fechaSeleccionada
        else
            "No seleccionada"

        // Construir mensaje con todos los datos
        val mensaje = "Nombre: $nombre\n" +
                "Comida favorita: $comida\n" +
                "Pasatiempo: $pasatiempo\n" +
                "Practica deporte: $practicaDeporte\n" +
                "Fecha de nacimiento: $fecha"

        // Mostrar AlertDialog con los resultados
        AlertDialog.Builder(this)
            .setTitle("Resultados de la Encuesta")
            .setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}