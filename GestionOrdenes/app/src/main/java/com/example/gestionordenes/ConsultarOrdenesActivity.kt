package com.example.gestionordenes

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gestionordenes.database.DatabaseHelper
import com.example.gestionordenes.models.*

class ConsultarOrdenesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listViewOrdenes: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var ordenes = mutableListOf<Orden>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_ordenes)

        dbHelper = DatabaseHelper(this)
        listViewOrdenes = findViewById(R.id.listViewOrdenes)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listViewOrdenes.adapter = adapter

        listViewOrdenes.setOnItemClickListener { _, _, position, _ ->
            val orden = ordenes[position]
            mostrarDetallesOrden(orden)
        }

        cargarOrdenes()
    }

    private fun cargarOrdenes() {
        ordenes.clear()
        ordenes.addAll(dbHelper.obtenerOrdenes())

        if (ordenes.isEmpty()) {
            adapter.clear()
            adapter.add("No hay órdenes registradas")
            adapter.notifyDataSetChanged()
            return
        }

        val ordenesTexto = ordenes.map { orden ->
            val nombreCliente = dbHelper.obtenerNombreCliente(orden.idCliente)
            "Orden #${orden.idOrden}\nCliente: $nombreCliente\nFecha: ${orden.fecha}\nTotal: $${String.format("%.2f", orden.total)}"
        }

        adapter.clear()
        adapter.addAll(ordenesTexto)
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDetallesOrden(orden: Orden) {
        val nombreCliente = dbHelper.obtenerNombreCliente(orden.idCliente)
        val detalles = dbHelper.obtenerDetallesOrden(orden.idOrden)

        val mensaje = StringBuilder().apply {
            append("Orden #${orden.idOrden}\n")
            append("Cliente: $nombreCliente\n")
            append("Fecha: ${orden.fecha}\n\n")
            append("Productos:\n")

            detalles.forEach { detalle ->
                val nombreProducto = dbHelper.obtenerNombreProducto(detalle.idProducto)
                append("• $nombreProducto\n")
                append("  Cantidad: ${detalle.cantidad}\n")
                append("  Precio: $${String.format("%.2f", detalle.precioUnitario)}\n")
                append("  Subtotal: $${String.format("%.2f", detalle.subtotal)}\n\n")
            }

            append("TOTAL: $${String.format("%.2f", orden.total)}")
        }

        AlertDialog.Builder(this)
            .setTitle("Detalles de la Orden")
            .setMessage(mensaje.toString())
            .setPositiveButton("Cerrar", null)
            .show()
    }
}