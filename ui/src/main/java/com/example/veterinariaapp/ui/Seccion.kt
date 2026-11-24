package com.example.veterinariaapp.ui

// Destinos del menú. La sección activa vive en el ViewModel. Son cuatro pantallas, así que no se usa una librería de navegación.
enum class Seccion(val etiqueta: String) {
    INICIO("Inicio"),
    CLIENTES("Dueños y mascotas"),
    PRODUCTOS("Productos"),
    PEDIDOS("Pedidos")
}
