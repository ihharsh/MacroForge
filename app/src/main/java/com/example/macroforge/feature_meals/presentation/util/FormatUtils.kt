package com.example.macroforge.feature_meals.presentation.util

import com.example.macroforge.feature_meals.domain.model.MealTag

fun formatMacro(value: Float): String =
    if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)

fun MealTag.displayLabel(): String = when (this) {
    MealTag.BREAKFAST -> "Breakfast"
    MealTag.LUNCH -> "Lunch"
    MealTag.DINNER -> "Dinner"
    MealTag.SNACK -> "Snack"
    MealTag.PRE_WORKOUT -> "Pre Workout"
    MealTag.POST_WORKOUT -> "Post Workout"
}
