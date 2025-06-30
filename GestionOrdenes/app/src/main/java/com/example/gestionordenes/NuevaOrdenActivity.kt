package com.example.gestionordenes

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gestionordenes.database.DatabaseHelper
import com.example.gestionordenes.models.*
import java.text.SimpleDateFormat
import java.util.*

class NuevaOrdenActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerClientes: Spinner
    private lateinit var spinnerProductos: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var btnAgregarProducto: Button
    private lateinit var btnFinalizarOrden: Button
    private lateinit var listViewCarrito: ListView
    private lateinit var tvTotal: TextView

    private var clientes = listOf<Cliente>()
    private var productos = listOf<Producto>()
    private var carrito = mutableListOf<ItemCarrito>()
    private lateinit var carritoAdapter: ArrayAdapter<String>

    data class ItemCarrito(
        val producto: Producto,
        var cantidad: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_orden)

        dbHelper = DatabaseHelper(this)
        inicializarComponentes()
        cargarDatos()
        configurarEventos()
    }

    private fun inicializarComponentes() {
        spinnerClientes = findViewById(R.id.spinnerClientes)
        spinnerProductos = findViewById(R.id.spinnerProductos)
        etCantidad = findViewById(R.id.etCantidad)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
        btnFinalizarOrden = findViewById(R.id.btnFinalizarOrden)
        listViewCarrito = findViewById(R.id.listViewCarrito)
        tvTotal = findViewById(R.id.tvTotal)

        carritoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listViewCarrito.adapter = carritoAdapter
        actualizarTotal()
    }

    private fun cargarDatos() {
        clientes = dbHelper.obtenerClientes()
        productos = dbHelper.obtenerProductos()

        if (clientes.isEmpty()) {
            Toast.makeText(this, "No hay clientes", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (productos.isEmpty()) {
            Toast.makeText(this, "No hay productos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val nombresClientes = clientes.map { it.nombre }
        val clientesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresClientes)
        spinnerClientes.adapter = clientesAdapter

        val nombresProductos = productos.map { "${it.nombreProducto} - $${it.precio}" }
        val productosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresProductos)
        spinnerProductos.adapter = productosAdapter
    }

    private fun configurarEventos() {
        btnAgregarProducto.setOnClickListener {
            agregarProductoAlCarrito()
        }

        btnFinalizarOrden.setOnClickListener {
            finalizarOrden()
        }
    }

    private fun agregarProductoAlCarrito() {
        val cantidadStr = etCantidad.text.toString()

        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cantidad = cantidadStr.toInt()
            if (cantidad <= 0) {
                Toast.makeText(this, "Cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val productoSeleccionado = productos[spinnerProductos.selectedItemPosition]

            if (cantidad > productoSeleccionado.stock) {
                Toast.makeText(this, "Stock insuficiente", Toast.LENGTH_SHORT).show()
                return
            }

            val itemExistente = carrito.find { it.producto.idProducto == productoSeleccionado.idProducto }

            if (itemExistente != null) {
                itemExistente.cantidad += cantidad
            } else {
                carrito.add(ItemCarrito(productoSeleccionado, cantidad))
            }

            etCantidad.setText("")
            actualizarCarrito()
            actualizarTotal()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarCarrito() {
        val carritoTexto = carrito.map { item ->
            "${item.producto.nombreProducto} - Cant: ${item.cantidad}"
        }

        carritoAdapter.clear()
        carritoAdapter.addAll(carritoTexto)
        carritoAdapter.notifyDataSetChanged()
    }

    private fun actualizarTotal() {
        val total = carrito.sumOf { it.producto.precio * it.cantidad }
        tvTotal.text = "Total: $${String.format("%.2f", total)}"
        btnFinalizarOrden.isEnabled = carrito.isNotEmpty()
    }

    private fun finalizarOrden() {
        if (carrito.isEmpty()) return

        val clienteSeleccionado = clientes[spinnerClientes.selectedItemPosition]
        val total = carrito.sumOf { it.producto.precio * it.cantidad }
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val orden = Orden(
            idCliente = clienteSeleccionado.idCliente,
            fecha = fecha,
            total = total
        )

        val idOrden = dbHelper.insertarOrden(orden)

        if (idOrden > 0) {
            carrito.forEach { item ->
                val detalle = DetalleOrden(
                    idOrden = idOrden.toInt(),
                    idProducto = item.producto.idProducto,
                    cantidad = item.cantidad,
                    precioUnitario = item.producto.precio
                )
                dbHelper.insertarDetalleOrden(detalle)
            }

            Toast.makeText(this, "Orden creada exitosamente", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Error al crear orden", Toast.LENGTH_SHORT).show()
        }
    }
}