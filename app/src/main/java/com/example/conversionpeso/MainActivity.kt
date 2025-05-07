package com.example.conversionpeso

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etPeso: EditText
    private lateinit var spinnerUnidades: Spinner
    private lateinit var btnConvertir: Button
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPeso = findViewById(R.id.etPeso)
        spinnerUnidades = findViewById(R.id.spinnerUnidades)
        btnConvertir = findViewById(R.id.btnConvertir)
        tvResultado = findViewById(R.id.tvResultado)

        val opciones = arrayOf(
            "Kilogramos a Libras",
            "Kilogramos a Onzas",
            "Libras a Kilogramos",
            "Onzas a Kilogramos"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinnerUnidades.adapter = adapter

        btnConvertir.setOnClickListener {
            val valorTexto = etPeso.text.toString()
            val valor = valorTexto.toFloatOrNull()

            if (valor == null) {
                Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = when (spinnerUnidades.selectedItem.toString()) {
                "Kilogramos a Libras" -> valor * 2.20462
                "Kilogramos a Onzas" -> valor * 35.274
                "Libras a Kilogramos" -> valor / 2.20462
                "Onzas a Kilogramos" -> valor / 35.274
                else -> 0.0
            }

            val unidad = when (spinnerUnidades.selectedItem.toString()) {
                "Kilogramos a Libras" -> "lb"
                "Kilogramos a Onzas" -> "oz"
                "Libras a Kilogramos" -> "kg"
                "Onzas a Kilogramos" -> "kg"
                else -> ""
            }

            tvResultado.text = "Resultado: %.2f $unidad".format(resultado)
        }
    }
}