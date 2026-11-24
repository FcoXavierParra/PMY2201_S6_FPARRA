package com.example.veterinariaapp.datos

import com.example.veterinariaapp.modelo.Categoria
import com.example.veterinariaapp.modelo.Cliente
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.Pedido
import com.example.veterinariaapp.modelo.Producto
import com.example.veterinariaapp.servicio.Veterinaria
import java.time.LocalDate

// Catálogo, clientes, mascotas y pedidos con que parte la jornada de la veterinaria
class DatosIniciales {
    val consulta = Producto("Consulta general", 15990, Categoria.SERVICIO)
    val vacuna = Producto("Vacuna antirrábica", 12990, Categoria.SERVICIO)
    val antiparasitario = Producto("Antiparasitario", 8490, Categoria.MEDICAMENTO)
    val alimento = Producto("Alimento premium 3 kg", 25990, Categoria.ALIMENTO)
    val collar = Producto("Collar", 6990, Categoria.ACCESORIO)
    val catalogo: List<Producto> = listOf(consulta, vacuna, antiparasitario, alimento, collar)

    val juan = Cliente("Juan Pérez", "+56 (912) 345-678", "juan.perez@mail.com", "Calle Moneda 1020")
    val maria = Cliente("María López", "+56 (922) 334-455", "maria@mail.com", "Av. Independencia 500")
    val firulais = Mascota("Firulais", "Perro", juan)
    val michi = Mascota("Michi", "Gato", maria)

    private val fechaPedidosPrevios: LocalDate = LocalDate.of(2026, 11, 3)

    val veterinaria: Veterinaria

    init {
        // Stock final: ya descontado por los pedidos previos (la jornada empieza con estos saldos)
        veterinaria = Veterinaria(
            catalogo,
            mapOf(
                consulta.nombre to 49,
                vacuna.nombre to 9,
                antiparasitario.nombre to 9,
                alimento.nombre to 4,
                collar.nombre to 0
            )
        )
        veterinaria.registrarPedidoPrevio(Pedido(juan, listOf(consulta, antiparasitario), fechaPedidosPrevios))
        veterinaria.registrarPedidoPrevio(Pedido(juan, listOf(alimento), fechaPedidosPrevios))
        veterinaria.registrarPedidoPrevio(Pedido(maria, listOf(vacuna), fechaPedidosPrevios))

        veterinaria.registrarMascota(firulais)
        veterinaria.registrarMascota(michi)
        veterinaria.registrarConsulta(firulais, fechaPedidosPrevios, "Control anual")
    }
}
