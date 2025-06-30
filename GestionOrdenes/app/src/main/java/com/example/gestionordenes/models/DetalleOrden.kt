package com.example.gestionordenes.models

/**
 * Modelo de datos para representar el detalle de una orden
 * @param idDetalle Identificador único del detalle (auto-generado por la BD)
 * @param idOrden Referencia a la orden principal
 * @param idProducto Referencia al producto ordenado
 * @param cantidad Cantidad del producto ordenado
 * @param precioUnitario Precio unitario del producto al momento de la orden
 * @param subtotal Subtotal calculado (cantidad * precioUnitario)
 */
data class DetalleOrden(
    val idDetalle: Int = 0,
    val idOrden: Int,
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double = cantidad * precioUnitario
)