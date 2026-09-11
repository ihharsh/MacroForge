package com.example.macroforge.feature_meals.domain.model

import com.example.macroforge.feature_foods.domain.model.Food

// A single food + the quantity of it used within a meal.
// Replaces raw MealFoodCrossRef (a Room-only join row) outside the data layer.
data class MealFoodEntry(
    val food: Food,
    val quantity: Float
)
