package com.example.gestionordenes.models

/**
 * Modelo de datos para representar una Orden de compra
 * @param idOrden Identificador único de la orden (auto-generado por la BD)
 * @param idCliente Referencia al cliente que realizó la orden
 * @param fecha Fecha de creación de la orden (formato: yyyy-MM-dd)
 * @param total Monto total de la orden
 * @param estado Estado actual de la orden (Pendiente, Procesando, Completada, etc.)
 */
data class Orden(
    val idOrden: Int = 0,
    val idCliente: Int,
    val fecha: String,
    val total: Double = 0.0,
    val estado: String = "Pendiente"
)