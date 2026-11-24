package com.example.veterinariaapp.modelo

// Servicio o producto que la veterinaria vende. El stock se controla en Veterinaria.
class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: Categoria
) {
    init {
        require(precio > 0) { "El precio de $nombre debe ser mayor a 0" }
    }

    // Mismo producto = mismo nombre, sin importar mayúsculas
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.equals(other.nombre, ignoreCase = true)
    }

    override fun hashCode(): Int = nombre.lowercase().hashCode()

    override fun toString(): String = "$nombre (${categoria.etiqueta})"
}
