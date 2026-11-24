# Veterinaria · Avanzando hacia Android Studio

**Actividad:** Semana 6 · Actividad sumativa individual · Desarrollo App Móviles I (PMY2201)
**Estudiante:** Francisco Parra
**Estado:** entrega sumativa. Ejecutada y verificada en emulador (Pixel 4, API 37). Retroalimentación de S5 pendiente.

Aplicación Android en Kotlin para la gestión de una veterinaria (dueños, mascotas, productos, pedidos y consultas). En S6 se agregan animaciones Fade In/Out, un menú desplegable en la barra superior y un indicador de progreso ligado a LiveData.

---

## 1. Qué se agregó sobre S5

| Requisito (instrucciones S6) | Implementación | Archivo |
|---|---|---|
| Paso 1 · Fade In al cargar y Fade Out al cambiar de sección, con `AnimatedVisibility` | El contenido aparece con `fadeIn` al terminar la carga. Cada sección entra y sale con `fadeIn`/`fadeOut` | `ui/PantallaVeterinaria.kt` |
| Paso 2 · Menú `DropdownMenu` con íconos, desde un botón de la barra superior | `TopAppBar` con botón ⋮. Cuatro opciones con ícono: Inicio, Dueños y mascotas, Productos, Pedidos | `ui/PantallaVeterinaria.kt` |
| Paso 3 · Indicador de progreso con mensaje, ligado a LiveData | `CircularProgressIndicator` con "Cargando datos..." al abrir la app y "Generando resumen..." al pulsar "Actualizar resumen" | `ui/VeterinariaViewModel.kt`, `ui/PantallaVeterinaria.kt` |
| Paso 4 · Integración | El menú cambia la sección activa del ViewModel. Los datos de carga vienen del módulo `datos` | `ui/VeterinariaViewModel.kt`, `app/MainActivity.kt` |
| Paso 5 · Pruebas en AVD | Ejecución en Pixel 4 (API 37), con capturas de cada funcionalidad (sección 6) | `evidencias/` |
| Paso 6 · Entrega | Proyecto, capturas y ZIP | `evidencias/`, `../PMY2201_S6_FPARRA.zip` |

Lo que **no cambia** respecto de S5: módulos `datos`, `utilidades`, la lógica de `Veterinaria`, los supuestos S1 a S4, el catálogo de versiones y las 19 pruebas unitarias.

## 2. Decisiones

1. **`DropdownMenu`, no menú lateral.** La guía de la semana enseña un `ModalNavigationDrawer`, pero las instrucciones y la pauta piden un `DropdownMenu` desde la barra superior. Se siguió la pauta.
2. **Navegación sin librería.** La sección activa es un valor del ViewModel (`Seccion`). Son cuatro destinos, así que `navigation-compose` agregaba complejidad sin aportar a la pauta.
3. **Secciones.** Inicio (bienvenida, resumen y botón de actualizar), Dueños y mascotas (incluye registrar consulta), Productos (stock y precios) y Pedidos (formulario, confirmar y pedidos despachados).
4. **Formulario de pedido fuera de la sección.** Los campos viven en `PantallaVeterinaria`, así que no se pierden al cambiar de menú.
5. **Progreso con demora simulada.** `VeterinariaViewModel.refrescar()` marca `cargando = true`, espera 1,2 s (`DEMORA_CARGA_MS`) y luego publica el estado. La demora **representa** una lectura de datos: los datos siguen en memoria (ver sección 5.1).
6. **Íconos.** Se agregó `material-icons-extended` al catálogo de versiones para los íconos del menú.

## 3. Arquitectura

```
VeterinariaApp/
├── app/          Entrada: MainActivity (observa LiveData y reenvía acciones), recurso de bienvenida
├── ui/           PantallaVeterinaria (barra, menú, animaciones, progreso), Secciones (contenido), Seccion, VeterinariaViewModel, tema
├── datos/        Dominio: modelo, servicio Veterinaria, DatosIniciales
└── utilidades/   Validaciones, formato de montos, Resultado
```

**Dependencias:** `app → ui → datos → utilidades` y `ui → utilidades`. Sin ciclos.

**Flujo:** el ViewModel publica `estado`, `seccion`, `cargando` y `textoCarga` como `LiveData`. `MainActivity` los observa y pasa a `PantallaVeterinaria` esos valores y las acciones. La pantalla no contiene lógica de negocio.

## 4. Pruebas

```
gradlew testDebugUnitTest
```

| Módulo | Archivo | Pruebas | Resultado |
|---|---|---|---|
| utilidades | `FormatoTest`, `ValidacionesTest` | 6 | Pasan |
| datos | `VeterinariaTest` | 13 | Pasan |
| **Total** | | **19** | **Pasan** |

**Pruebas en emulador (Pixel 4, API 37):**
- Inicio: la app abre con el progreso "Cargando datos..." y luego aparece el contenido.
- Menú: se abre con los cuatro íconos; "Pedidos" cambia el título y muestra el formulario.
- Actualizar resumen: aparece "Generando resumen..." y luego el resumen vuelve a mostrarse.
- Sin errores de la app en logcat (`FATAL EXCEPTION` y `ANR in com.example.veterinariaapp`, en cero).

## 5. Limitaciones conocidas

### 5.1 Los datos están en memoria
Igual que en S5: al cerrar la app, todo vuelve a `DatosIniciales`. Girar la pantalla no los pierde, porque el ViewModel los conserva.

### 5.2 La demora de carga es simulada
El progreso dura 1,2 s por diseño, para mostrar el indicador. No hay lectura de disco ni de red: la "carga" es una corrutina con `delay`. Esto permite ver la integración del indicador con LiveData, pero no mide rendimiento real.

### 5.3 Las animaciones se verifican con capturas, no con mediciones
Las capturas muestran el resultado (progreso, menú abierto, sección nueva), pero no el movimiento. El rendimiento de las transiciones no se midió con frames; en el emulador (renderizado por software) puede verse entrecortado.

### 5.4 No hay pruebas instrumentadas
Las transiciones, el menú y el progreso se verificaron a mano en el emulador. No hay `createComposeRule` que confirme automáticamente que el menú cambia de sección.

### 5.5 Otras limitaciones
- Los pedidos pendientes por falta de stock no se guardan; solo se informa el faltante.
- El formulario de pedido no se conserva al girar la pantalla. Sí se conserva al cambiar de sección.
- En S5 las Coroutines solo envolvían acciones en memoria. En S6 la demora simulada le da a la corrutina un uso visible, pero sigue sin haber trabajo asíncrono real (ver 5.2).

## 6. Evidencias

Emulador **Pixel 4, API 37**, con el APK de depuración.

| # | Archivo | Qué muestra |
|---|---|---|
| E1 | `evidencias/E1_progreso_generando_resumen.png` | Progreso "Generando resumen..." al pulsar actualizar |
| E2 | `evidencias/E2_inicio_fade_in.png` | Sección Inicio con bienvenida, resumen y botón |
| E3 | `evidencias/E3_menu_desplegable.png` | Menú desplegable abierto con los cuatro íconos |
| E4 | `evidencias/E4_seccion_pedidos.png` | Sección Pedidos tras navegar desde el menú |
| E5 | `evidencias/E5_resumen_tras_progreso.png` | Inicio con el resumen de nuevo, después del progreso |
| E6 | `evidencias/E6_estructura_modular.txt` | Archivos fuente de cada módulo |
| E7 | `evidencias/E7_pruebas.txt` | Resultado de las 19 pruebas unitarias |
| E8 | `evidencias/E8_demo_navegacion.mp4` | Grabación corta del emulador: abre el menú, navega a Pedidos, vuelve a Inicio y pulsa "Actualizar resumen" (progreso) |

**Pendiente:** captura de la estructura modular en Android Studio (panel de proyecto, con paquetes expandidos) y captura del AVD en Device Manager, como en S5.

## 7. Retroalimentación

La retroalimentación de S5 llegará en los próximos días. Si cambia algo de la base reutilizada (paquetes, arquitectura, pruebas), se revisará en esta entrega.
