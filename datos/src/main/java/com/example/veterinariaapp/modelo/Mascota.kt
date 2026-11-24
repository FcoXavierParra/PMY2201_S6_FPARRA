package com.example.veterinariaapp.modelo

// Paciente de la veterinaria. Pertenece a un dueño (Cliente).
class Mascota(
    val nombre: String,
    val especie: String,
    val dueno: Cliente
) {
    init {
        require(nombre.isNotBlank()) { "La mascota debe tener nombre" }
    }

    // Misma mascota = mismo nombre y mismo dueño
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mascota) return false
        return nombre.equals(other.nombre, ignoreCase = true) && dueno == other.dueno
    }

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + dueno.hashCode()

    override fun toString(): String = "$nombre ($especie) de ${dueno.nombre}"
}
