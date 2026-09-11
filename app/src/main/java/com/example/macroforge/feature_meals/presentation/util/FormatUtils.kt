package com.example.macroforge.feature_meals.presentation.util

fun formatMacro(value: Float): String =
    if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)