package com.example.veterinariaapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatoTest {

    @Test
    fun `montos se separan por miles`() {
        assertEquals("$38.513", formatearPesos(38513))
        assertEquals("$999", formatearPesos(999))
        assertEquals("$1.000.000", formatearPesos(1000000))
    }
}
