package com.example.macroforge.feature_foods.data.mapper

import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.feature_foods.domain.model.Food

fun FoodEntity.toDomain(): Food = Food(
    foodId = foodId,
    foodName = foodName,
    baseNumber = baseNumber,
    unitType = unitType,
    calories = calories,
    carbs = carbs,
    protein = protein,
    fats = fats,
    isCustom = isCustom,
    ownerId = ownerId,
    updatedAt = updatedAt
)

fun Food.toEntity(): FoodEntity = FoodEntity(
    foodId = foodId,
    foodName = foodName,
    baseNumber = baseNumber,
    unitType = unitType,
    calories = calories,
    carbs = carbs,
    protein = protein,
    fats = fats,
    isCustom = isCustom,
    ownerId = ownerId,
    updatedAt = updatedAt
)
