package com.example.veterinariaapp.utils

// nombre@dominio.com:
//  - usuario: segmentos separados por un punto, sin punto al inicio, al final ni dos seguidos
//  - dominio: segmentos que empiezan y terminan en letra o número (guiones solo al medio)
//  - extensión de al menos 2 letras
private const val SEGMENTO_USUARIO = "[a-z0-9_%+-]+"
private const val SEGMENTO_DOMINIO = "[a-z0-9]+(-+[a-z0-9]+)*"
private val PATRON_CORREO =
    Regex("^$SEGMENTO_USUARIO(\\.$SEGMENTO_USUARIO)*@$SEGMENTO_DOMINIO(\\.$SEGMENTO_DOMINIO)*\\.[a-z]{2,}$")

// Solo dígitos, espacios, guiones, paréntesis y un + inicial
private val PATRON_TELEFONO_ENTRADA = Regex("^\\+?[\\d\\s()-]+$")
// Código de país (2) + área (3) + central (3) + número (3 o 4)
private val PATRON_TELEFONO_DIGITOS = Regex("^(\\d{2})(\\d{3})(\\d{3})(\\d{3,4})$")

private const val CODIGO_PAIS_POR_DEFECTO = "56"

// Devuelve el correo normalizado o null si no cumple el formato
fun validarCorreo(correo: String?): String? {
    val normalizado = correo?.trim()?.lowercase() ?: return null
    return normalizado.takeIf { PATRON_CORREO.matches(it) }
}

// Formato uniforme +XX (XXX) XXX-XXXX. Sin código de país se asume +56.
// Ejemplo: +56 (912) 345-678
fun formatearTelefono(telefono: String?): String? {
    val texto = telefono?.trim() ?: return null
    if (!PATRON_TELEFONO_ENTRADA.matches(texto)) return null
    val digitos = texto.filter { it.isDigit() }
    val completo = if (digitos.length in 9..10) CODIGO_PAIS_POR_DEFECTO + digitos else digitos
    val (pais, area, central, numero) = PATRON_TELEFONO_DIGITOS.matchEntire(completo)?.destructured ?: return null
    return "+$pais ($area) $central-$numero"
}

// "  ana   PÉREZ " -> "Ana Pérez"; null si queda vacío
fun formatearNombre(nombre: String?): String? =
    nombre?.trim()
        ?.split(Regex("\\s+"))
        ?.joinToString(" ") { palabra -> palabra.lowercase().replaceFirstChar { it.uppercase() } }
        ?.ifEmpty { null }
