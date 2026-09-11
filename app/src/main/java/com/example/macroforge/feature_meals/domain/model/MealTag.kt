package com.example.macroforge.feature_meals.domain.model

// Domain enum — plain Kotlin, no Room dependency. Persistence-layer conversion
// lives in MealTagConverter (core/data/local/converter), not here.
enum class MealTag {
    BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT
}
