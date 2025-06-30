package com.example.gestionordenes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnClientes).setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        findViewById<Button>(R.id.btnProductos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        findViewById<Button>(R.id.btnNuevaOrden).setOnClickListener {
            startActivity(Intent(this, NuevaOrdenActivity::class.java))
        }

        findViewById<Button>(R.id.btnConsultarOrdenes).setOnClickListener {
            startActivity(Intent(this, ConsultarOrdenesActivity::class.java))
        }
    }
}