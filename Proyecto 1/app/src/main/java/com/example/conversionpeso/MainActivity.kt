package com.example.conversionpeso

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etValor: EditText
    private lateinit var spinnerOrigen: Spinner
    private lateinit var spinnerDestino: Spinner
    private lateinit var btnConvertir: Button
    private lateinit var tvResultado: TextView

    private val unidades = arrayOf("Kilogramos", "Libras", "Onzas", "Gramos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etValor = findViewById(R.id.etValor)
        spinnerOrigen = findViewById(R.id.spinnerOrigen)
        spinnerDestino = findViewById(R.id.spinnerDestino)
        btnConvertir = findViewById(R.id.btnConvertir)
        tvResultado = findViewById(R.id.tvResultado)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        spinnerOrigen.adapter = adapter
        spinnerDestino.adapter = adapter

        btnConvertir.setOnClickListener {
            val valorTexto = etValor.text.toString()
            val valor = valorTexto.toDoubleOrNull()

            if (valor == null) {
                Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val origen = spinnerOrigen.selectedItem.toString()
            val destino = spinnerDestino.selectedItem.toString()
            val resultado = convertirPeso(valor, origen, destino)
            val resultadoFormateado = formatearResultado(resultado, destino)

            tvResultado.text = "Resultado: $resultadoFormateado $destino"
        }
    }

    private fun convertirPeso(valor: Double, origen: String, destino: String): Double {
        val enGramos = when (origen) {
            "Kilogramos" -> valor * 1000.0
            "Libras"     -> valor * 453.592
            "Onzas"      -> valor * 28.3495
            "Gramos"     -> valor
            else         -> 0.0
        }

        return when (destino) {
            "Kilogramos" -> enGramos / 1000.0
            "Libras"     -> enGramos / 453.592
            "Onzas"      -> enGramos / 28.3495
            "Gramos"     -> enGramos
            else         -> 0.0
        }
    }

    private fun formatearResultado(valor: Double, unidad: String): String {
        return when (unidad) {
            "Kilogramos", "Libras" -> String.format("%.4f", valor)
            "Onzas"                -> String.format("%.3f", valor)
            "Gramos"               -> valor.toInt().toString()
            else                   -> valor.toString()
        }
    }
}