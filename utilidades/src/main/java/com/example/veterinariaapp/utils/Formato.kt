package com.example.veterinariaapp.utils

// 38513 -> "$38.513"
fun formatearPesos(monto: Int): String =
    "$" + monto.toString().reversed().chunked(3).joinToString(".").reversed()
