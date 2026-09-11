package com.example.macroforge.feature_meals.domain.model

import com.example.macroforge.feature_foods.domain.model.Food

// Domain model for a meal — framework-agnostic (no Room/Compose/Firebase).
// Carries its resolved food entries directly, so callers (use cases, ViewModels)
// don't need to re-join foods against cross-refs themselves.
data class Meal(
    val mealId: String,
    val mealName: String,
    val mealRecipe: String?,
    val mealTag: MealTag,
    val ownerId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String,
    val entries: List<MealFoodEntry>
) {
    val foods: List<Food>
        get() = entries.map { it.food }

    val quantitiesByFoodId: Map<String, Float>
        get() = entries.associate { it.food.foodId to it.quantity }
}
