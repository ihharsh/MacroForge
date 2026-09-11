package com.example.macroforge.core.data.local.dto

import com.example.macroforge.feature_foods.domain.model.Food
import kotlinx.serialization.Serializable

// Shape of assets/seed_foods.json — kept independent of FoodEntity so the
// Room schema and the seed file format can change without breaking each other.
@Serializable
data class SeedFoodDto(
    val foodId: String,
    val foodName: String,
    val baseNumber: Float,
    val unitType: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fats: Float
)

fun SeedFoodDto.toDomain(): Food = Food(
    foodId = foodId,
    foodName = foodName,
    baseNumber = baseNumber,
    unitType = unitType,
    calories = calories,
    carbs = carbs,
    protein = protein,
    fats = fats
)
