package com.example.veterinariaapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidacionesTest {

    @Test
    fun `correo valido se normaliza a minusculas`() {
        assertEquals("ana.perez@mail.com", validarCorreo("  Ana.Perez@Mail.com "))
    }

    @Test
    fun `correos invalidos se rechazan`() {
        assertNull(validarCorreo("ana..perez@mail.com"))
        assertNull(validarCorreo(".ana@mail.com"))
        assertNull(validarCorreo("ana@-mail.com"))
        assertNull(validarCorreo("ana@mail.c"))
        assertNull(validarCorreo(null))
    }

    @Test
    fun `telefono sin codigo de pais se formatea con +56`() {
        assertEquals("+56 (912) 345-678", formatearTelefono("912345678"))
    }

    @Test
    fun `telefono con letras se rechaza`() {
        assertNull(formatearTelefono("912-abc-678"))
    }

    @Test
    fun `nombre se capitaliza y se limpian espacios`() {
        assertEquals("Ana Pérez", formatearNombre("  ana   PÉREZ "))
        assertNull(formatearNombre("   "))
    }
}
