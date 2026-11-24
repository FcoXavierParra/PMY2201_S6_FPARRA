package com.example.veterinariaapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.modelo.Mascota
import com.example.veterinariaapp.modelo.Producto
import com.example.veterinariaapp.utils.formatearPesos
import com.example.veterinariaapp.utils.formatearTelefono
import com.example.veterinariaapp.utils.validarCorreo
import kotlinx.coroutines.launch

// Contenido de cada sección del menú. Son funciones sin estado: reciben datos y reportan acciones.

@Composable
fun InicioSection(
    encabezado: @Composable () -> Unit,
    estado: EstadoVeterinaria,
    onActualizar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { encabezado() }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium)
                    Text("Clientes registrados: ${estado.resumen.totalClientes}")
                    Text("Pedidos realizados: ${estado.resumen.totalPedidos}")
                    Text("Último cliente registrado: ${estado.resumen.ultimoCliente ?: "—"}")
                }
            }
        }
        item {
            Button(onClick = onActualizar, modifier = Modifier.fillMaxWidth()) { Text("Actualizar resumen") }
        }
        item {
            Text(
                "Usa el menú (⋮) de la barra superior para ir a Dueños y mascotas, Productos o Pedidos.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ClientesSection(
    estado: EstadoVeterinaria,
    onRegistrarConsulta: (Mascota) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Titulo("Dueños (${estado.clientes.size})") }
        items(estado.clientes, key = { it.correo }) { cliente ->
            Text("${cliente.nombre} · ${cliente.correo} · ${cliente.telefono}")
        }
        item { Titulo("Mascotas (${estado.mascotas.size})") }
        items(estado.mascotas, key = { "${it.nombre}-${it.dueno.correo}" }) { mascota ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${mascota.nombre} (${mascota.especie}) · dueño: ${mascota.dueno.nombre}", modifier = Modifier.padding(end = 8.dp))
                OutlinedButton(onClick = { onRegistrarConsulta(mascota) }) { Text("Registrar consulta") }
            }
        }
        item { Titulo("Consultas (${estado.consultas.size})") }
        items(estado.consultas, key = { "${it.fecha}-${it.mascota.nombre}-${it.motivo}" }) { consulta ->
            Text(consulta.toString())
        }
        estado.mensaje?.let { mensaje -> item { Text(mensaje, modifier = Modifier.padding(top = 8.dp)) } }
    }
}

@Composable
fun ProductosSection(estado: EstadoVeterinaria, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Titulo("Productos y servicios (${estado.stock.size})") }
        items(estado.stock.keys.toList(), key = { it.nombre }) { producto ->
            Column {
                Text(producto.nombre)
                Text(
                    "${formatearPesos(producto.precio)} · ${producto.categoria} · stock ${estado.stock[producto] ?: 0}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PedidosSection(
    estado: EstadoVeterinaria,
    nombre: String,
    onNombre: (String) -> Unit,
    correo: String,
    onCorreo: (String) -> Unit,
    telefono: String,
    onTelefono: (String) -> Unit,
    direccion: String,
    onDireccion: (String) -> Unit,
    cantidades: Map<Producto, Int>,
    onCantidad: (Producto, Int) -> Unit,
    onConfirmar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Se recalcula solo cuando cambian los campos del formulario
    val formularioCompleto = remember(nombre, correo, telefono, direccion) {
        nombre.isNotBlank() && validarCorreo(correo) != null &&
            formatearTelefono(telefono) != null && direccion.isNotBlank()
    }
    val hayProductos = cantidades.values.sum() > 0

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // Al enfocar un campo, la lista vuelve al bloque del formulario (primer ítem) para que se vea sobre el teclado
    val alEnfocar = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) scope.launch { listState.animateScrollToItem(0) } }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Titulo("Nuevo pedido")
                OutlinedTextField(value = nombre, onValueChange = onNombre, label = { Text("Nombre del dueño") }, modifier = alEnfocar, singleLine = true)
                OutlinedTextField(value = correo, onValueChange = onCorreo, label = { Text("Correo") }, modifier = alEnfocar, singleLine = true, isError = correo.isNotBlank() && validarCorreo(correo) == null)
                OutlinedTextField(value = telefono, onValueChange = onTelefono, label = { Text("Teléfono") }, modifier = alEnfocar, singleLine = true, isError = telefono.isNotBlank() && formatearTelefono(telefono) == null)
                OutlinedTextField(value = direccion, onValueChange = onDireccion, label = { Text("Dirección") }, modifier = alEnfocar, singleLine = true)
            }
        }

        items(estado.stock.keys.toList(), key = { it.nombre }) { producto ->
            FilaProducto(
                producto = producto,
                stock = estado.stock[producto] ?: 0,
                cantidad = cantidades[producto] ?: 0,
                alCambiar = { nueva -> onCantidad(producto, nueva.coerceIn(0, 10)) }
            )
        }

        item {
            Column {
                Button(onClick = onConfirmar, enabled = formularioCompleto && hayProductos, modifier = Modifier.fillMaxWidth()) {
                    Text("Confirmar pedido")
                }
                estado.mensaje?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
            }
        }

        item { Titulo("Pedidos despachados (${estado.pedidos.size})") }
        items(estado.pedidos, key = { it.id }) { pedido ->
            Text("#${pedido.id} · ${pedido.cliente.nombre} · ${pedido.fecha} · ${formatearPesos(pedido.calcularTotal().total)}")
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Column {
        Spacer(Modifier.height(8.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
    }
}

@Composable
private fun FilaProducto(producto: Producto, stock: Int, cantidad: Int, alCambiar: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(end = 8.dp)) {
            Text(producto.nombre)
            Text("${formatearPesos(producto.precio)} · stock $stock", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { alCambiar(cantidad - 1) }, enabled = cantidad > 0) { Text("−") }
            Text("$cantidad")
            TextButton(onClick = { alCambiar(cantidad + 1) }, enabled = cantidad < stock) { Text("+") }
        }
    }
}
