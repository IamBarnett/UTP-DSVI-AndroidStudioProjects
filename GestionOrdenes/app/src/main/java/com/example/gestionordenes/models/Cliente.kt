package com.example.gestionordenes.models

/**
 * Modelo de datos para representar un Cliente
 * @param idCliente Identificador único del cliente (auto-generado por la BD)
 * @param nombre Nombre completo del cliente
 * @param correo Correo electrónico del cliente (único)
 * @param telefono Número de teléfono del cliente
 * @param direccion Dirección física del cliente
 */
data class Cliente(
    val idCliente: Int = 0,
    val nombre: String,
    val correo: String,
    val telefono: String = "",
    val direccion: String = ""
)