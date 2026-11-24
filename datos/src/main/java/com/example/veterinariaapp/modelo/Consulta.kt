package com.example.veterinariaapp.modelo

import java.time.LocalDate

// Atención clínica de una mascota
class Consulta(
    val mascota: Mascota,
    val fecha: LocalDate,
    val motivo: String
) {
    init {
        require(motivo.isNotBlank()) { "La consulta debe indicar el motivo" }
    }

    override fun toString(): String = "$fecha · ${mascota.nombre}: $motivo"
}
