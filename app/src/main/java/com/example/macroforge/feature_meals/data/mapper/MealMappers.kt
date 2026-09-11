package com.example.macroforge.feature_meals.data.mapper

import com.example.macroforge.core.data.local.relation.MealWithFoods
import com.example.macroforge.feature_foods.data.mapper.toDomain
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealFoodEntry

// Resolves the Room relation (meal + foods + join rows) into a single domain
// Meal, matching each food to its quantity once — instead of every caller
// re-deriving that match (and re-scaling macros) independently.
fun MealWithFoods.toDomain(): Meal {
    val entries = foods.map { foodEntity ->
        val quantity = crossRefs.firstOrNull { it.foodId == foodEntity.foodId }?.quantity ?: 0f
        MealFoodEntry(food = foodEntity.toDomain(), quantity = quantity)
    }

    return Meal(
        mealId = meal.mealId,
        mealName = meal.mealName,
        mealRecipe = meal.mealRecipe,
        mealTag = meal.mealTag,
        ownerId = meal.ownerId,
        createdAt = meal.createdAt,
        updatedAt = meal.updatedAt,
        syncStatus = meal.syncStatus,
        entries = entries
    )
}
