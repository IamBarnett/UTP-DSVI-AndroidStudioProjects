package com.example.gestionordenes.models

/**
 * Modelo de datos para representar un Producto
 * @param idProducto Identificador único del producto (auto-generado por la BD)
 * @param nombreProducto Nombre del producto
 * @param precio Precio unitario del producto
 * @param descripcion Descripción detallada del producto
 * @param stock Cantidad disponible en inventario
 */
data class Producto(
    val idProducto: Int = 0,
    val nombreProducto: String,
    val precio: Double,
    val descripcion: String = "",
    val stock: Int = 0
)