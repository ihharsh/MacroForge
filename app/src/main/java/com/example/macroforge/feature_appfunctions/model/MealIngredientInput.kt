package com.example.macroforge.feature_appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

/**
 * One ingredient to add when creating a meal via `createMeal`.
 *
 * Always obtain [foodId] from a prior `searchFoods` call — never invent
 * one. [quantity] must be expressed in the same unit `searchFoods` reported
 * as that food's `servingUnit` (e.g. a count of 4 for a food whose
 * servingUnit is "egg", or 500 for a food whose servingUnit is "g"). Pass
 * [unit] only if you want it double-checked against the resolved food's
 * actual unit; a mismatch is rejected rather than silently reinterpreted.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class MealIngredientInput(
    /** The foodId returned by `searchFoods` for this ingredient. */
    val foodId: String,
    /** How many [MealIngredientInput.foodId]'s native serving units to use. Must be greater than zero. */
    val quantity: Float,
    /**
     * Optional safety check: the unit you believe [quantity] is expressed
     * in (e.g. "g", "egg"). If it does not match the resolved food's actual
     * unit, the request is rejected instead of silently misinterpreting the
     * quantity. Omit if not known.
     */
    val unit: String? = null,
)
