package com.example.gestionordenes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gestionordenes.models.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "GestionOrdenes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla Clientes
        db?.execSQL("""
            CREATE TABLE Clientes (
                idCliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL,
                telefono TEXT,
                direccion TEXT
            )
        """)

        // Tabla Productos
        db?.execSQL("""
            CREATE TABLE Productos (
                idProducto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreProducto TEXT NOT NULL,
                precio REAL NOT NULL,
                descripcion TEXT,
                stock INTEGER DEFAULT 0
            )
        """)

        // Tabla Órdenes
        db?.execSQL("""
            CREATE TABLE Ordenes (
                idOrden INTEGER PRIMARY KEY AUTOINCREMENT,
                idCliente INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                total REAL DEFAULT 0.0,
                estado TEXT DEFAULT 'Pendiente'
            )
        """)

        // Tabla DetalleOrden
        db?.execSQL("""
            CREATE TABLE DetalleOrden (
                idDetalle INTEGER PRIMARY KEY AUTOINCREMENT,
                idOrden INTEGER NOT NULL,
                idProducto INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precioUnitario REAL NOT NULL,
                subtotal REAL NOT NULL
            )
        """)

        // Datos de ejemplo
        insertarDatosEjemplo(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS DetalleOrden")
        db?.execSQL("DROP TABLE IF EXISTS Ordenes")
        db?.execSQL("DROP TABLE IF EXISTS Productos")
        db?.execSQL("DROP TABLE IF EXISTS Clientes")
        onCreate(db)
    }

    private fun insertarDatosEjemplo(db: SQLiteDatabase?) {
        // Clientes de ejemplo
        val clientes = listOf(
            "INSERT INTO Clientes (nombre, correo, telefono, direccion) VALUES ('Juan Pérez', 'juan@email.com', '6000-0001', 'Ciudad de Panamá')",
            "INSERT INTO Clientes (nombre, correo, telefono, direccion) VALUES ('María González', 'maria@email.com', '6000-0002', 'San Miguelito')",
            "INSERT INTO Clientes (nombre, correo, telefono, direccion) VALUES ('Carlos Rodríguez', 'carlos@email.com', '6000-0003', 'Chorrera')"
        )
        clientes.forEach { db?.execSQL(it) }

        // Productos de ejemplo
        val productos = listOf(
            "INSERT INTO Productos (nombreProducto, precio, descripcion, stock) VALUES ('Laptop Dell', 899.99, 'Laptop empresarial', 10)",
            "INSERT INTO Productos (nombreProducto, precio, descripcion, stock) VALUES ('Mouse Logitech', 25.50, 'Mouse inalámbrico', 50)",
            "INSERT INTO Productos (nombreProducto, precio, descripcion, stock) VALUES ('Teclado Mecánico', 89.99, 'Teclado RGB', 20)",
            "INSERT INTO Productos (nombreProducto, precio, descripcion, stock) VALUES ('Monitor 24', 199.99, 'Monitor Full HD', 15)"
        )
        productos.forEach { db?.execSQL(it) }
    }

    // CRUD Clientes
    fun insertarCliente(cliente: Cliente): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", cliente.nombre)
            put("correo", cliente.correo)
            put("telefono", cliente.telefono)
            put("direccion", cliente.direccion)
        }
        return db.insert("Clientes", null, values)
    }

    fun obtenerClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Clientes ORDER BY nombre", null)

        cursor.use {
            while (it.moveToNext()) {
                val cliente = Cliente(
                    idCliente = it.getInt(0),
                    nombre = it.getString(1),
                    correo = it.getString(2),
                    telefono = it.getString(3) ?: "",
                    direccion = it.getString(4) ?: ""
                )
                clientes.add(cliente)
            }
        }
        return clientes
    }

    fun eliminarCliente(idCliente: Int): Int {
        val db = writableDatabase
        return db.delete("Clientes", "idCliente = ?", arrayOf(idCliente.toString()))
    }

    // CRUD Productos
    fun insertarProducto(producto: Producto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombreProducto", producto.nombreProducto)
            put("precio", producto.precio)
            put("descripcion", producto.descripcion)
            put("stock", producto.stock)
        }
        return db.insert("Productos", null, values)
    }

    fun obtenerProductos(): List<Producto> {
        val productos = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Productos ORDER BY nombreProducto", null)

        cursor.use {
            while (it.moveToNext()) {
                val producto = Producto(
                    idProducto = it.getInt(0),
                    nombreProducto = it.getString(1),
                    precio = it.getDouble(2),
                    descripcion = it.getString(3) ?: "",
                    stock = it.getInt(4)
                )
                productos.add(producto)
            }
        }
        return productos
    }

    fun eliminarProducto(idProducto: Int): Int {
        val db = writableDatabase
        return db.delete("Productos", "idProducto = ?", arrayOf(idProducto.toString()))
    }

    // CRUD Órdenes
    fun insertarOrden(orden: Orden): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idCliente", orden.idCliente)
            put("fecha", orden.fecha)
            put("total", orden.total)
            put("estado", orden.estado)
        }
        return db.insert("Ordenes", null, values)
    }

    fun insertarDetalleOrden(detalle: DetalleOrden): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idOrden", detalle.idOrden)
            put("idProducto", detalle.idProducto)
            put("cantidad", detalle.cantidad)
            put("precioUnitario", detalle.precioUnitario)
            put("subtotal", detalle.subtotal)
        }
        return db.insert("DetalleOrden", null, values)
    }

    fun obtenerOrdenes(): List<Orden> {
        val ordenes = mutableListOf<Orden>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Ordenes ORDER BY fecha DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                val orden = Orden(
                    idOrden = it.getInt(0),
                    idCliente = it.getInt(1),
                    fecha = it.getString(2),
                    total = it.getDouble(3),
                    estado = it.getString(4)
                )
                ordenes.add(orden)
            }
        }
        return ordenes
    }

    fun obtenerDetallesOrden(idOrden: Int): List<DetalleOrden> {
        val detalles = mutableListOf<DetalleOrden>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM DetalleOrden WHERE idOrden = ?", arrayOf(idOrden.toString()))

        cursor.use {
            while (it.moveToNext()) {
                val detalle = DetalleOrden(
                    idDetalle = it.getInt(0),
                    idOrden = it.getInt(1),
                    idProducto = it.getInt(2),
                    cantidad = it.getInt(3),
                    precioUnitario = it.getDouble(4),
                    subtotal = it.getDouble(5)
                )
                detalles.add(detalle)
            }
        }
        return detalles
    }

    fun obtenerNombreCliente(idCliente: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Clientes WHERE idCliente = ?", arrayOf(idCliente.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return ""
    }

    fun obtenerNombreProducto(idProducto: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombreProducto FROM Productos WHERE idProducto = ?", arrayOf(idProducto.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return ""
    }
}