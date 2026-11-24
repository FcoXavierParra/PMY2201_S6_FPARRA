package com.example.veterinariaapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.datos.DatosIniciales
import com.example.veterinariaapp.modelo.Cliente
import com.example.veterinariaapp.modelo.Consulta
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.Pedido
import com.example.veterinariaapp.modelo.Producto
import com.example.veterinariaapp.servicio.ResumenVeterinaria
import com.example.veterinariaapp.servicio.Veterinaria
import com.example.veterinariaapp.utils.Resultado
import com.example.veterinariaapp.utils.formatearNombre
import com.example.veterinariaapp.utils.formatearTelefono
import com.example.veterinariaapp.utils.validarCorreo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

// Todo lo que la pantalla muestra. Se publica completo cada vez que algo cambia.
data class EstadoVeterinaria(
    val resumen: ResumenVeterinaria,
    val clientes: List<Cliente>,
    val mascotas: List<Mascota>,
    val consultas: List<Consulta>,
    val pedidos: List<Pedido>,
    val stock: Map<Producto, Int>,
    val mensaje: String? = null
)

// La veterinaria vive en el ViewModel (no en remember), así sobrevive a la rotación de pantalla.
class VeterinariaViewModel(private val datos: DatosIniciales = DatosIniciales()) : ViewModel() {

    private val veterinaria: Veterinaria = datos.veterinaria
    private val _estado = MutableLiveData(construir(null))
    val estado: LiveData<EstadoVeterinaria> = _estado

    // Sección activa del menú
    private val _seccion = MutableLiveData(Seccion.INICIO)
    val seccion: LiveData<Seccion> = _seccion

    // Progreso: true mientras se cargan datos. Empieza en true para mostrar el indicador al abrir la app.
    private val _cargando = MutableLiveData(true)
    val cargando: LiveData<Boolean> = _cargando

    private val _textoCarga = MutableLiveData("Cargando datos...")
    val textoCarga: LiveData<String> = _textoCarga

    init {
        refrescar("Cargando datos...")
    }

    val catalogo: List<Producto>
        get() = veterinaria.catalogo

    fun irA(seccion: Seccion) {
        _seccion.value = seccion
    }

    // Vuelve a leer los datos del módulo de datos. La demora simula una carga; los datos siguen en memoria.
    fun refrescar(texto: String = "Generando resumen...") {
        viewModelScope.launch {
            _textoCarga.value = texto
            _cargando.value = true
            delay(DEMORA_CARGA_MS)
            _estado.value = construir(null)
            _cargando.value = false
        }
    }

    fun confirmarPedido(
        nombre: String,
        correo: String,
        telefono: String,
        direccion: String,
        cantidades: Map<Producto, Int>
    ) {
        viewModelScope.launch {
            val nombreOk = formatearNombre(nombre)
            val correoOk = validarCorreo(correo)
            val telefonoOk = formatearTelefono(telefono)
            val direccionOk = direccion.trim().ifEmpty { null }
            val mensaje = when {
                nombreOk == null || correoOk == null || telefonoOk == null || direccionOk == null ->
                    "Revisa nombre, correo, teléfono y dirección"
                cantidades.values.sum() == 0 -> "Agrega al menos un producto"
                else -> {
                    val productos = cantidades.flatMap { (producto, n) -> List(n) { producto } }
                    val cliente = Cliente(nombreOk, telefonoOk, correoOk, direccionOk)
                    val resultado = veterinaria.confirmarPedido(cliente, productos, LocalDate.now())
                    if (resultado.despachado) {
                        "Pedido #${resultado.pedido.id} despachado para ${resultado.registro.cliente.nombre}"
                    } else {
                        "No se despachó. Falta stock de: " + resultado.faltantes.joinToString { f ->
                            "${f.producto.nombre} (pidió ${f.solicitados}, hay ${f.disponibles})"
                        }
                    }
                }
            }
            _estado.value = construir(mensaje)
        }
    }

    fun registrarConsulta(mascota: Mascota, motivo: String) {
        viewModelScope.launch {
            val mensaje = when (val r = veterinaria.registrarConsulta(mascota, LocalDate.now(), motivo)) {
                is Resultado.Exito -> "Consulta registrada para ${mascota.nombre}"
                is Resultado.Error -> r.mensaje
            }
            _estado.value = construir(mensaje)
        }
    }

    private fun construir(mensaje: String?) = EstadoVeterinaria(
        resumen = veterinaria.resumen(),
        clientes = veterinaria.clientes(),
        mascotas = veterinaria.mascotas(),
        consultas = veterinaria.consultas(),
        pedidos = veterinaria.pedidos(),
        stock = veterinaria.catalogo.associateWith { veterinaria.stock(it) },
        mensaje = mensaje
    )

    companion object {
        const val DEMORA_CARGA_MS = 1200L
    }
}
