package com.example.veterinariaapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.Producto

// Pantalla principal: barra superior con menú desplegable, contenido animado por sección y progreso de carga.
// No conoce el ViewModel: recibe el estado y reporta las acciones (MainActivity hace la conexión).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVeterinaria(
    encabezado: @Composable () -> Unit,
    estado: EstadoVeterinaria,
    seccion: Seccion,
    cargando: Boolean,
    textoCarga: String,
    onSeleccionar: (Seccion) -> Unit,
    onActualizar: () -> Unit,
    onConfirmarPedido: (nombre: String, correo: String, telefono: String, direccion: String, cantidades: Map<Producto, Int>) -> Unit,
    onRegistrarConsulta: (Mascota) -> Unit,
    modifier: Modifier = Modifier
) {
    // El formulario vive aquí (fuera de la sección) para que no se pierda al cambiar de menú
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    val cantidades = remember { mutableStateMapOf<Producto, Int>() }

    Scaffold(
        modifier = modifier,
        topBar = { BarraSuperior(seccion = seccion, onSeleccionar = onSeleccionar) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Progreso: aparece mientras se cargan datos y desaparece con fade out
            AnimatedVisibility(
                visible = cargando,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(300))
            ) {
                PanelCarga(textoCarga)
            }

            // Contenido: aparece con fade in al terminar la carga
            AnimatedVisibility(
                visible = !cargando,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(500)),
                exit = fadeOut(tween(200))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Cada sección entra y sale con fade in/out al cambiar en el menú
                    Seccion.entries.forEach { s ->
                        AnimatedVisibility(
                            visible = seccion == s,
                            modifier = Modifier.fillMaxSize(),
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(200))
                        ) {
                            when (s) {
                                Seccion.INICIO -> InicioSection(
                                    encabezado = encabezado,
                                    estado = estado,
                                    onActualizar = onActualizar
                                )
                                Seccion.CLIENTES -> ClientesSection(
                                    estado = estado,
                                    onRegistrarConsulta = onRegistrarConsulta
                                )
                                Seccion.PRODUCTOS -> ProductosSection(estado = estado)
                                Seccion.PEDIDOS -> PedidosSection(
                                    estado = estado,
                                    nombre = nombre,
                                    onNombre = { nombre = it },
                                    correo = correo,
                                    onCorreo = { correo = it },
                                    telefono = telefono,
                                    onTelefono = { telefono = it },
                                    direccion = direccion,
                                    onDireccion = { direccion = it },
                                    cantidades = cantidades,
                                    onCantidad = { producto, n -> cantidades[producto] = n },
                                    onConfirmar = {
                                        onConfirmarPedido(nombre, correo, telefono, direccion, cantidades.toMap())
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Barra superior: título de la sección y un botón que abre el menú desplegable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSuperior(seccion: Seccion, onSeleccionar: (Seccion) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(seccion.etiqueta) },
        actions = {
            Box {
                IconButton(onClick = { abierto = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Abrir menú")
                }
                DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                    Seccion.entries.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.etiqueta) },
                            leadingIcon = { Icon(s.icono(), contentDescription = null) },
                            onClick = {
                                onSeleccionar(s)
                                abierto = false
                            }
                        )
                    }
                }
            }
        }
    )
}

private fun Seccion.icono(): ImageVector = when (this) {
    Seccion.INICIO -> Icons.Filled.Home
    Seccion.CLIENTES -> Icons.Filled.Person
    Seccion.PRODUCTOS -> Icons.Filled.Inventory2
    Seccion.PEDIDOS -> Icons.Filled.ShoppingCart
}

@Composable
private fun PanelCarga(texto: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(texto, modifier = Modifier.padding(top = 16.dp))
    }
}
