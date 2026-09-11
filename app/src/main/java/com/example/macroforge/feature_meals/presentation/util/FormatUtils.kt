package com.example.macroforge.feature_meals.presentation.util

import com.example.macroforge.feature_meals.domain.model.MealTag

fun MealTag.displayLabel(): String = when (this) {
    MealTag.BREAKFAST -> "Breakfast"
    MealTag.LUNCH -> "Lunch"
    MealTag.DINNER -> "Dinner"
    MealTag.SNACK -> "Snack"
    MealTag.PRE_WORKOUT -> "Pre Workout"
    MealTag.POST_WORKOUT -> "Post Workout"
}
