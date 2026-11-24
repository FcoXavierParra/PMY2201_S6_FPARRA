package com.example.veterinariaapp.servicio

import com.example.veterinariaapp.modelo.Cliente
import com.example.veterinariaapp.modelo.Consulta
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.Pedido
import com.example.veterinariaapp.modelo.Producto
import com.example.veterinariaapp.modelo.Registro
import com.example.veterinariaapp.utils.Resultado
import java.time.LocalDate

// Reglas de la veterinaria: clientes (dueños), mascotas, consultas, pedidos y stock.
class Veterinaria(
    val catalogo: List<Producto>,
    stockInicial: Map<String, Int>
) {
    private val stock: MutableMap<String, Int> = stockInicial.toMutableMap()
    private val clientes = Registro<Cliente>()
    private val mascotas = Registro<Mascota>()
    private val consultas = mutableListOf<Consulta>()
    private val pedidos = mutableListOf<Pedido>()

    // Último cliente NUEVO registrado (supuesto V3): reutilizar un cliente no lo convierte en "último"
    private var ultimoClienteNuevo: Cliente? = null

    fun clientes(): List<Cliente> = clientes.todos()
    fun mascotas(): List<Mascota> = mascotas.todos()
    fun consultas(): List<Consulta> = consultas.toList()

    // Solo pedidos DESPACHADOS (supuesto V4)
    fun pedidos(): List<Pedido> = pedidos.toList()

    fun stock(producto: Producto): Int = stock[producto.nombre] ?: 0

    // Si ya existe un cliente con el mismo nombre y correo, se reutiliza y se actualizan sus datos de contacto
    fun registrarCliente(cliente: Cliente): ResultadoRegistro {
        clientes.buscar(cliente)?.let { existente ->
            return ResultadoRegistro(existente, esNuevo = false, datosActualizados = existente.actualizarContacto(cliente))
        }
        clientes.agregar(cliente)
        ultimoClienteNuevo = cliente
        return ResultadoRegistro(cliente, esNuevo = true)
    }

    fun registrarMascota(mascota: Mascota): Resultado<Mascota> {
        if (clientes.buscar(mascota.dueno) == null) {
            return Resultado.Error("El dueño ${mascota.dueno.nombre} no está registrado")
        }
        if (!mascotas.agregar(mascota)) {
            return Resultado.Error("${mascota.nombre} ya está registrada para ${mascota.dueno.nombre}")
        }
        return Resultado.Exito(mascota)
    }

    fun registrarConsulta(mascota: Mascota, fecha: LocalDate, motivo: String): Resultado<Consulta> {
        if (mascotas.buscar(mascota) == null) {
            return Resultado.Error("${mascota.nombre} no está registrada")
        }
        if (motivo.isBlank()) {
            return Resultado.Error("La consulta debe indicar el motivo")
        }
        val consulta = Consulta(mascota, fecha, motivo.trim())
        consultas += consulta
        return Resultado.Exito(consulta)
    }

    // Productos pedidos que no alcanzan en stock. Lista vacía = el pedido se puede despachar
    fun faltantes(productos: List<Producto>): List<Faltante> =
        productos.groupingBy { it }.eachCount().mapNotNull { (producto, solicitados) ->
            val disponibles = stock(producto)
            if (disponibles < solicitados) Faltante(producto, solicitados, disponibles) else null
        }

    // Flujo completo: registra (o reutiliza) al cliente. Si hay stock de todo, descuenta, despacha y guarda el pedido.
    // Si falta algo, nada cambia en stock ni en pedidos: el resultado informa qué falta.
    fun confirmarPedido(
        cliente: Cliente,
        productos: List<Producto>,
        fecha: LocalDate,
        notas: String? = null
    ): ResultadoPedido {
        val registro = registrarCliente(cliente)
        val faltantes = faltantes(productos)
        val pedido = Pedido(registro.cliente, productos, fecha, notas)
        if (faltantes.isEmpty()) {
            productos.forEach { stock[it.nombre] = stock(it) - 1 }
            pedido.despachar()
            pedidos += pedido
        }
        return ResultadoPedido(pedido, faltantes, registro)
    }

    // Pedido ya realizado antes de iniciar la jornada: se guarda como despachado sin tocar el stock,
    // porque el stock inicial ya refleja su consumo
    fun registrarPedidoPrevio(pedido: Pedido) {
        registrarCliente(pedido.cliente)
        pedido.despachar()
        pedidos += pedido
    }

    fun resumen(): ResumenVeterinaria = ResumenVeterinaria(
        totalClientes = clientes.cantidad,
        totalPedidos = pedidos.size,
        ultimoCliente = ultimoClienteNuevo?.nombre
    )
}

data class Faltante(val producto: Producto, val solicitados: Int, val disponibles: Int)

// Lo que muestra el resumen de la pantalla inicial
data class ResumenVeterinaria(
    val totalClientes: Int,
    val totalPedidos: Int,
    val ultimoCliente: String?
)
