package com.example.gestionordenes

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gestionordenes.database.DatabaseHelper
import com.example.gestionordenes.models.Cliente

class ClientesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listViewClientes: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var clientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        dbHelper = DatabaseHelper(this)
        listViewClientes = findViewById(R.id.listViewClientes)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listViewClientes.adapter = adapter

        findViewById<Button>(R.id.btnAgregarCliente).setOnClickListener {
            mostrarDialogoCliente()
        }

        listViewClientes.setOnItemLongClickListener { _, _, position, _ ->
            val cliente = clientes[position]
            confirmarEliminacion(cliente)
            true
        }

        cargarClientes()
    }

    private fun cargarClientes() {
        clientes.clear()
        clientes.addAll(dbHelper.obtenerClientes())

        val clientesTexto = clientes.map {
            "${it.nombre} - ${it.correo}"
        }

        adapter.clear()
        adapter.addAll(clientesTexto)
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDialogoCliente() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cliente, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etTelefono = dialogView.findViewById<EditText>(R.id.etTelefono)
        val etDireccion = dialogView.findViewById<EditText>(R.id.etDireccion)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Cliente")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val correo = etCorreo.text.toString()
                val telefono = etTelefono.text.toString()
                val direccion = etDireccion.text.toString()

                if (nombre.isNotEmpty() && correo.isNotEmpty()) {
                    val cliente = Cliente(
                        nombre = nombre,
                        correo = correo,
                        telefono = telefono,
                        direccion = direccion
                    )
                    dbHelper.insertarCliente(cliente)
                    cargarClientes()
                    Toast.makeText(this, "Cliente agregado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminacion(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Eliminar a ${cliente.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                dbHelper.eliminarCliente(cliente.idCliente)
                cargarClientes()
                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}