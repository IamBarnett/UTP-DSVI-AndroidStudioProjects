package com.tunombre.conversionpeso

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tunombre.conversionpeso.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val unidades = arrayOf("Kilogramos a Libras", "Kilogramos a Onzas", "Libras a Kilogramos", "Onzas a Kilogramos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        binding.spinnerUnidades.adapter = adapter

        binding.btnConvertir.setOnClickListener {
            val valor = binding.etPeso.text.toString().toFloatOrNull()

            if (valor == null) {
                Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = when (binding.spinnerUnidades.selectedItem.toString()) {
                "Kilogramos a Libras" -> valor * 2.20462
                "Kilogramos a Onzas" -> valor * 35.274
                "Libras a Kilogramos" -> valor / 2.20462
                "Onzas a Kilogramos" -> valor / 35.274
                else -> 0.0
            }

            val unidadDestino = when (binding.spinnerUnidades.selectedItem.toString()) {
                "Kilogramos a Libras" -> "lb"
                "Kilogramos a Onzas" -> "oz"
                "Libras a Kilogramos" -> "kg"
                "Onzas a Kilogramos" -> "kg"
                else -> ""
            }

            binding.tvResultado.text = "Resultado: %.2f $unidadDestino".format(resultado)
        }
    }
}