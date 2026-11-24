package com.example.veterinariaapp.servicio

import com.example.veterinariaapp.modelo.EstadoPedido
import com.example.veterinariaapp.modelo.Pedido

// Lo que la pantalla necesita mostrar después de confirmar un pedido
data class ResultadoPedido(
    val pedido: Pedido,
    val faltantes: List<Faltante>,
    val registro: ResultadoRegistro
) {
    val despachado: Boolean
        get() = pedido.estado == EstadoPedido.DESPACHADO
}
