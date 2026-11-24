package com.example.veterinariaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.ui.PantallaVeterinaria
import com.example.veterinariaapp.ui.Seccion
import com.example.veterinariaapp.ui.VeterinariaViewModel
import com.example.veterinariaapp.ui.theme.VeterinariaTheme

// Conecta la lógica (VeterinariaViewModel) con la pantalla: observa el estado y reenvía las acciones del usuario
class MainActivity : ComponentActivity() {

    private val viewModel: VeterinariaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VeterinariaTheme {
                val estado = viewModel.estado.observeAsState().value
                val seccion = viewModel.seccion.observeAsState(Seccion.INICIO).value
                val cargando = viewModel.cargando.observeAsState(true).value
                val textoCarga = viewModel.textoCarga.observeAsState("Cargando datos...").value
                if (estado != null) {
                    PantallaVeterinaria(
                        encabezado = { Greeting() },
                        estado = estado,
                        seccion = seccion,
                        cargando = cargando,
                        textoCarga = textoCarga,
                        onSeleccionar = { viewModel.irA(it) },
                        onActualizar = { viewModel.refrescar() },
                        onConfirmarPedido = { nombre, correo, telefono, direccion, cantidades ->
                            viewModel.confirmarPedido(nombre, correo, telefono, direccion, cantidades)
                        },
                        onRegistrarConsulta = { mascota -> viewModel.registrarConsulta(mascota, "Control de rutina") },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

// Mensaje de bienvenida (texto en res/values/strings.xml)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.mensaje_bienvenida),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = stringResource(R.string.nombre_veterinaria), style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VeterinariaTheme {
        Greeting()
    }
}
