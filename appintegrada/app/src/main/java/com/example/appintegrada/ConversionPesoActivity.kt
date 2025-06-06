package com.example.appintegrada

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class ConversionPesoActivity : AppCompatActivity() {

    private lateinit var editTextKilogramos: EditText
    private lateinit var editTextLibras: EditText
    private lateinit var btnKgALibras: Button
    private lateinit var btnLibrasAKg: Button
    private lateinit var btnLimpiar: Button
    private lateinit var textViewResultado: TextView

    private val factorConversion = 2.20462
    private val formatoDecimal = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion_peso)

        supportActionBar?.title = "Conversión de Peso"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inicializarComponentes()
        configurarEventos()
    }

    private fun inicializarComponentes() {
        editTextKilogramos = findViewById(R.id.editTextKilogramos)
        editTextLibras = findViewById(R.id.editTextLibras)
        btnKgALibras = findViewById(R.id.btnKgALibras)
        btnLibrasAKg = findViewById(R.id.btnLibrasAKg)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        textViewResultado = findViewById(R.id.textViewResultado)
    }

    private fun configurarEventos() {
        btnKgALibras.setOnClickListener {
            convertirKgALibras()
        }

        btnLibrasAKg.setOnClickListener {
            convertirLibrasAKg()
        }

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
    }

    private fun convertirKgALibras() {
        val kgTexto = editTextKilogramos.text.toString().trim()

        if (kgTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese un valor en kilogramos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val kg = kgTexto.toDouble()

            if (kg < 0) {
                Toast.makeText(this, "El peso no puede ser negativo", Toast.LENGTH_SHORT).show()
                return
            }

            val libras = kg * factorConversion
            val resultado = formatoDecimal.format(libras)

            editTextLibras.setText(resultado)
            textViewResultado.text = "$kg kg = $resultado lbs"

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertirLibrasAKg() {
        val librasTexto = editTextLibras.text.toString().trim()

        if (librasTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese un valor en libras", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val libras = librasTexto.toDouble()

            if (libras < 0) {
                Toast.makeText(this, "El peso no puede ser negativo", Toast.LENGTH_SHORT).show()
                return
            }

            val kg = libras / factorConversion
            val resultado = formatoDecimal.format(kg)

            editTextKilogramos.setText(resultado)
            textViewResultado.text = "$libras lbs = $resultado kg"

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        editTextKilogramos.text.clear()
        editTextLibras.text.clear()
        textViewResultado.text = "Resultado aparecerá aquí"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}