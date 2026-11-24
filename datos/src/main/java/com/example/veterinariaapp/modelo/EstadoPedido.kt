package com.example.veterinariaapp.modelo

// Supuesto V4: un pedido solo cuenta como realizado cuando queda DESPACHADO.
// Un pedido sin stock no se guarda como realizado; se informa el faltante en pantalla.
enum class EstadoPedido(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    DESPACHADO("Despachado")
}
