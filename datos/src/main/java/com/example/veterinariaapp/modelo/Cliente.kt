package com.example.veterinariaapp.modelo

// Dueño de una o más mascotas. Nombre y correo lo identifican; teléfono y dirección se pueden actualizar.
class Cliente(
    val nombre: String,
    telefono: String,
    val correo: String,
    direccion: String
) {
    var telefono: String = telefono
        private set

    var direccion: String = direccion
        private set

    // Criterio de actualización: al reutilizar un cliente, teléfono y dirección se reemplazan por los nuevos.
    // Devuelve los campos que cambiaron (vacío si eran iguales).
    fun actualizarContacto(nuevo: Cliente): List<String> {
        require(this == nuevo) { "solo se actualizan los datos del mismo cliente ($this y $nuevo)" }
        val cambios = mutableListOf<String>()
        if (telefono != nuevo.telefono) {
            telefono = nuevo.telefono
            cambios += "teléfono"
        }
        if (direccion != nuevo.direccion) {
            direccion = nuevo.direccion
            cambios += "dirección"
        }
        return cambios
    }

    // Mismo cliente = mismo nombre y mismo correo, sin importar mayúsculas
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.equals(other.nombre, ignoreCase = true) && correo.equals(other.correo, ignoreCase = true)
    }

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + correo.lowercase().hashCode()

    override fun toString(): String = "$nombre <$correo>"
}
