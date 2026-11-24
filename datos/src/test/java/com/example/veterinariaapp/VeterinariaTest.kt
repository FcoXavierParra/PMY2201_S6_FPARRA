package com.example.veterinariaapp

import com.example.veterinariaapp.datos.DatosIniciales
import com.example.veterinariaapp.modelo.Cliente
import com.example.veterinariaapp.modelo.EstadoPedido
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.calcularTotales
import com.example.veterinariaapp.utils.Resultado
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class VeterinariaTest {

    private val hoy = LocalDate.of(2026, 11, 19)

    private fun nuevoCliente(nombre: String = "Pedro Soto", correo: String = "pedro@mail.com") =
        Cliente(nombre, "+56 (933) 111-222", correo, "Calle Falsa 123")

    // --- Resumen inicial (supuestos V2 y V3) ---

    @Test
    fun `resumen inicial cuenta clientes, pedidos previos y ultimo cliente nuevo`() {
        val resumen = DatosIniciales().veterinaria.resumen()
        assertEquals(2, resumen.totalClientes)
        assertEquals(3, resumen.totalPedidos)
        assertEquals("María López", resumen.ultimoCliente)
    }

    // --- Clientes (V3) ---

    @Test
    fun `cliente nuevo suma al total y pasa a ser el ultimo`() {
        val datos = DatosIniciales()
        val resultado = datos.veterinaria.registrarCliente(nuevoCliente())
        assertTrue(resultado.esNuevo)
        val resumen = datos.veterinaria.resumen()
        assertEquals(3, resumen.totalClientes)
        assertEquals("Pedro Soto", resumen.ultimoCliente)
    }

    @Test
    fun `cliente reutilizado no suma y no cambia el ultimo cliente`() {
        val datos = DatosIniciales()
        val resultado = datos.veterinaria.registrarCliente(nuevoCliente("María López", "maria@mail.com"))
        assertFalse(resultado.esNuevo)
        val resumen = datos.veterinaria.resumen()
        assertEquals(2, resumen.totalClientes)
        assertEquals("María López", resumen.ultimoCliente)
    }

    @Test
    fun `cliente reutilizado actualiza telefono y direccion`() {
        val datos = DatosIniciales()
        val nuevo = Cliente("María López", "+56 (900) 000-111", "maria@mail.com", "Nueva dirección 9")
        val resultado = datos.veterinaria.registrarCliente(nuevo)
        assertEquals(listOf("teléfono", "dirección"), resultado.datosActualizados)
    }

    // --- Mascotas y consultas ---

    @Test
    fun `mascota de dueno no registrado se rechaza`() {
        val datos = DatosIniciales()
        val mascota = Mascota("Rex", "Perro", nuevoCliente())
        assertIs<Resultado.Error>(datos.veterinaria.registrarMascota(mascota))
    }

    @Test
    fun `mascota repetida se rechaza`() {
        val datos = DatosIniciales()
        assertIs<Resultado.Error>(datos.veterinaria.registrarMascota(datos.firulais))
    }

    @Test
    fun `consulta de mascota registrada se guarda`() {
        val datos = DatosIniciales()
        val antes = datos.veterinaria.consultas().size
        val resultado = datos.veterinaria.registrarConsulta(datos.michi, hoy, "Vacunación")
        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(antes + 1, datos.veterinaria.consultas().size)
    }

    @Test
    fun `consulta sin motivo o de mascota no registrada se rechaza`() {
        val datos = DatosIniciales()
        assertIs<Resultado.Error>(datos.veterinaria.registrarConsulta(datos.michi, hoy, "  "))
        assertIs<Resultado.Error>(datos.veterinaria.registrarConsulta(Mascota("Rex", "Perro", datos.juan), hoy, "Control"))
    }

    // --- Pedidos con stock (supuesto V4) ---

    @Test
    fun `pedido con stock se despacha, descuenta stock y cuenta como realizado`() {
        val datos = DatosIniciales()
        val v = datos.veterinaria
        val resultado = v.confirmarPedido(datos.juan, listOf(datos.vacuna, datos.vacuna), hoy)
        assertTrue(resultado.despachado)
        assertEquals(EstadoPedido.DESPACHADO, resultado.pedido.estado)
        assertEquals(9 - 2, v.stock(datos.vacuna))
        assertEquals(4, v.pedidos().size)
        assertEquals(4, v.resumen().totalPedidos)
    }

    @Test
    fun `pedido sin stock queda pendiente, informa faltante y no descuenta nada`() {
        val datos = DatosIniciales()
        val v = datos.veterinaria
        val resultado = v.confirmarPedido(datos.maria, listOf(datos.collar), hoy)
        assertFalse(resultado.despachado)
        assertEquals(EstadoPedido.PENDIENTE, resultado.pedido.estado)
        assertEquals(1, resultado.faltantes.size)
        assertEquals(0, resultado.faltantes.first().disponibles)
        assertEquals(0, v.stock(datos.collar))
        assertEquals(3, v.resumen().totalPedidos)
    }

    @Test
    fun `pedido que supera el stock disponible no se despacha`() {
        val datos = DatosIniciales()
        // Hay 4 unidades de alimento: pedir 5 debe fallar
        val resultado = datos.veterinaria.confirmarPedido(datos.juan, List(5) { datos.alimento }, hoy)
        assertFalse(resultado.despachado)
        assertEquals(5, resultado.faltantes.first().solicitados)
        assertEquals(4, datos.veterinaria.stock(datos.alimento))
    }

    @Test
    fun `pedido despachado no se vuelve a procesar`() {
        val datos = DatosIniciales()
        val pedido = datos.veterinaria.confirmarPedido(datos.juan, listOf(datos.vacuna), hoy).pedido
        assertFailsWithIllegalState { pedido.despachar() }
    }

    // --- Totales con IVA ---

    @Test
    fun `total suma IVA del 19 por ciento redondeado`() {
        val datos = DatosIniciales()
        val totales = calcularTotales(listOf(datos.consulta))
        assertEquals(15990, totales.subtotal)
        assertEquals(3038, totales.iva)
        assertEquals(19028, totales.total)
    }

    private fun assertFailsWithIllegalState(bloque: () -> Unit) {
        try {
            bloque()
        } catch (e: IllegalStateException) {
            return
        }
        throw AssertionError("Se esperaba IllegalStateException")
    }
}
