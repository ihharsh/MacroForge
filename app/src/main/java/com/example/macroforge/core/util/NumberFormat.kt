package com.example.macroforge.core.util

fun formatMacro(value: Float): String =
    if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)
