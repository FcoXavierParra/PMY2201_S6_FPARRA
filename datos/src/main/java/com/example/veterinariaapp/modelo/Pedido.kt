package com.example.veterinariaapp.modelo

import java.time.LocalDate

// Compra de productos o servicios de un dueño. Nace PENDIENTE y pasa a DESPACHADO cuando hay stock.
class Pedido(
    val cliente: Cliente,
    val productos: List<Producto>,
    val fecha: LocalDate,
    val notas: String? = null
) {
    init {
        require(productos.isNotEmpty()) { "El pedido debe tener al menos un producto" }
    }

    val id: Int = siguienteId++

    var estado: EstadoPedido = EstadoPedido.PENDIENTE
        private set

    fun calcularTotal(): Totales = calcularTotales(productos)

    fun despachar() {
        check(estado == EstadoPedido.PENDIENTE) { "Pedido #$id ya fue ${estado.etiqueta.lowercase()}" }
        estado = EstadoPedido.DESPACHADO
    }

    companion object {
        private var siguienteId = 1001
    }
}
