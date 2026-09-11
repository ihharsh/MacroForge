package com.example.macroforge.feature_meals.domain.usecase

import com.example.macroforge.feature_foods.domain.model.Food
import javax.inject.Inject

// feature_meals/domain/usecase/CalculateMealMacrosUseCase.kt
data class MacroTotals(
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fats: Float
)

class CalculateMealMacrosUseCase @Inject constructor() {

    operator fun invoke(foods: List<Food>, quantities: Map<String, Float>): MacroTotals {
        var cal = 0f; var carb = 0f; var protein = 0f; var fat = 0f

        foods.forEach { food ->
            val qty = quantities[food.foodId] ?: 0f
            val ratio = qty / food.baseNumber
            cal += food.calories * ratio
            carb += food.carbs * ratio
            protein += food.protein * ratio
            fat += food.fats * ratio
        }

        return MacroTotals(cal, carb, protein, fat)
    }
}
