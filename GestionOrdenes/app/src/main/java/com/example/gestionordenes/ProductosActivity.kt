package com.example.gestionordenes

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gestionordenes.database.DatabaseHelper
import com.example.gestionordenes.models.Producto

class ProductosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listViewProductos: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var productos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        dbHelper = DatabaseHelper(this)
        listViewProductos = findViewById(R.id.listViewProductos)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listViewProductos.adapter = adapter

        findViewById<Button>(R.id.btnAgregarProducto).setOnClickListener {
            mostrarDialogoProducto()
        }

        listViewProductos.setOnItemLongClickListener { _, _, position, _ ->
            val producto = productos[position]
            confirmarEliminacion(producto)
            true
        }

        cargarProductos()
    }

    private fun cargarProductos() {
        productos.clear()
        productos.addAll(dbHelper.obtenerProductos())

        val productosTexto = productos.map {
            "${it.nombreProducto} - $${it.precio}"
        }

        adapter.clear()
        adapter.addAll(productosTexto)
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDialogoProducto() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_producto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreProducto)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        val etStock = dialogView.findViewById<EditText>(R.id.etStock)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val precioStr = etPrecio.text.toString()
                val stockStr = etStock.text.toString()
                val descripcion = etDescripcion.text.toString()

                if (nombre.isNotEmpty() && precioStr.isNotEmpty()) {
                    try {
                        val precio = precioStr.toDouble()
                        val stock = if (stockStr.isNotEmpty()) stockStr.toInt() else 0

                        val producto = Producto(
                            nombreProducto = nombre,
                            precio = precio,
                            descripcion = descripcion,
                            stock = stock
                        )
                        dbHelper.insertarProducto(producto)
                        cargarProductos()
                        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminacion(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Eliminar ${producto.nombreProducto}?")
            .setPositiveButton("Eliminar") { _, _ ->
                dbHelper.eliminarProducto(producto.idProducto)
                cargarProductos()
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}