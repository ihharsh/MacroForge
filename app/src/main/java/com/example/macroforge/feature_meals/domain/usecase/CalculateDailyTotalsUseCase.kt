package com.example.macroforge.feature_meals.domain.usecase

import com.example.macroforge.feature_meals.domain.model.Meal
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

// Sums macros for meals created on the same local calendar day as `now`,
// regardless of any search/tag filter the caller may otherwise be applying.
class CalculateDailyTotalsUseCase @Inject constructor(
    private val calculateMealMacros: CalculateMealMacrosUseCase
) {
    operator fun invoke(meals: List<Meal>, now: Long = System.currentTimeMillis()): MacroTotals {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()

        return meals
            .filter { meal -> Instant.ofEpochMilli(meal.createdAt).atZone(zone).toLocalDate() == today }
            .fold(MacroTotals(0f, 0f, 0f, 0f)) { acc, meal ->
                val mealTotals = calculateMealMacros(meal.foods, meal.quantitiesByFoodId)
                MacroTotals(
                    calories = acc.calories + mealTotals.calories,
                    carbs = acc.carbs + mealTotals.carbs,
                    protein = acc.protein + mealTotals.protein,
                    fats = acc.fats + mealTotals.fats
                )
            }
    }
}
