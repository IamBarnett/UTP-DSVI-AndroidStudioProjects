package com.example.appintegrada

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ConversionMonedaActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var spinnerOrigen: Spinner
    private lateinit var spinnerDestino: Spinner
    private lateinit var btnConvertir: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnIntercambiar: Button
    private lateinit var tvResultado: TextView

    private val monedas = arrayOf("USD", "EUR", "PAB", "COP")
    private val tasasCambio = mapOf(
        "USD" to 1.0,
        "EUR" to 0.91,
        "PAB" to 1.0,
        "COP" to 4000.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion_moneda)

        supportActionBar?.title = "Conversión de Moneda"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMonto = findViewById(R.id.etMonto)
        spinnerOrigen = findViewById(R.id.spinnerOrigen)
        spinnerDestino = findViewById(R.id.spinnerDestino)
        btnConvertir = findViewById(R.id.btnConvertir)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        btnIntercambiar = findViewById(R.id.btnIntercambiar)
        tvResultado = findViewById(R.id.tvResultado)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monedas)
        spinnerOrigen.adapter = adapter
        spinnerDestino.adapter = adapter

        btnConvertir.setOnClickListener {
            val montoTexto = etMonto.text.toString().trim()
            val monto = montoTexto.toDoubleOrNull()
            val origen = spinnerOrigen.selectedItem.toString()
            val destino = spinnerDestino.selectedItem.toString()

            when {
                montoTexto.isEmpty() || monto == null -> {
                    Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                }
                monto <= 0 -> {
                    Toast.makeText(this, "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show()
                }
                origen == destino -> {
                    Toast.makeText(this, "Las monedas deben ser diferentes", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val tasaOrigen = tasasCambio[origen] ?: 1.0
                    val tasaDestino = tasasCambio[destino] ?: 1.0
                    val resultado = (monto / tasaOrigen) * tasaDestino
                    val simbolo = obtenerSimbolo(destino)
                    val resultadoFormateado = String.format("%.2f", resultado)
                    tvResultado.text = "$monto $origen = $resultadoFormateado $simbolo"
                }
            }
        }

        btnLimpiar.setOnClickListener {
            etMonto.text.clear()
            spinnerOrigen.setSelection(0)
            spinnerDestino.setSelection(0)
            tvResultado.text = "Resultado:"
        }

        btnIntercambiar.setOnClickListener {
            val posOrigen = spinnerOrigen.selectedItemPosition
            val posDestino = spinnerDestino.selectedItemPosition
            spinnerOrigen.setSelection(posDestino)
            spinnerDestino.setSelection(posOrigen)
        }
    }

    private fun obtenerSimbolo(moneda: String): String {
        return when (moneda) {
            "USD", "PAB" -> "$"
            "EUR" -> "€"
            "COP" -> "₱"
            else -> ""
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}