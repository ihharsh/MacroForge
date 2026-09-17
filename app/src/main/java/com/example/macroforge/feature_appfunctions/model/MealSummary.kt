package com.example.macroforge.feature_appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

/**
 * The result of creating a meal, or of looking one up again with `getMeal`.
 * Nutrition totals are computed the same way the app's own Create Meal
 * screen computes them — by summing each ingredient's macros scaled to its
 * saved quantity.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class MealSummary(
    /** The persisted meal's stable identifier. Pass this to `getMeal` to look it up again. */
    val mealId: String,
    /** The meal's name, as saved. */
    val mealName: String,
    /** The meal's tag, e.g. "BREAKFAST", "LUNCH", "DINNER", "SNACK", "PRE_WORKOUT", "POST_WORKOUT". */
    val mealTag: String,
    /** Every ingredient actually saved in this meal. */
    val ingredients: List<ResolvedIngredient>,
    /** Total calories across all ingredients. */
    val totalCalories: Float,
    /** Total protein in grams across all ingredients. */
    val totalProtein: Float,
    /** Total carbohydrates in grams across all ingredients. */
    val totalCarbs: Float,
    /** Total fat in grams across all ingredients. */
    val totalFats: Float,
)
