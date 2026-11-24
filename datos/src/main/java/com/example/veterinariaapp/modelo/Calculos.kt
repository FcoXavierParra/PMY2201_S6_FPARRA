package com.example.veterinariaapp.modelo

import kotlin.math.roundToInt

// IVA chileno: 19 %
const val TASA_IVA_PORCENTAJE = 19

// Totales en pesos enteros: subtotal, IVA (redondeado) y total
data class Totales(val subtotal: Int, val iva: Int) {
    val total: Int
        get() = subtotal + iva
}

fun calcularTotales(productos: List<Producto>): Totales {
    val subtotal = productos.sumOf { it.precio }
    val iva = (subtotal * TASA_IVA_PORCENTAJE / 100.0).roundToInt()
    return Totales(subtotal, iva)
}
