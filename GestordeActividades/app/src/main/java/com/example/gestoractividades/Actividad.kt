package com.example.gestoractividades

import java.io.Serializable

data class Actividad(
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val notificacion: Boolean
) : Serializable {

    override fun toString(): String {
        return "$titulo - $fecha $hora"
    }
}