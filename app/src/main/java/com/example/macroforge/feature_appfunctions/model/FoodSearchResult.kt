package com.example.macroforge.feature_appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

/**
 * One catalog match returned by `searchFoods`.
 *
 * `servingAmount`/`servingUnit` describe the food's own native serving —
 * e.g. servingAmount=1, servingUnit="egg" for a single egg, or
 * servingAmount=100, servingUnit="g" for a food measured by weight.
 * `caloriesPerServing` (and the other macros) are the nutrition values for
 * exactly that one serving, not per-gram — callers must scale by however
 * many servings they actually want when building a [MealIngredientInput].
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class FoodSearchResult(
    /** Stable identifier to pass as [MealIngredientInput.foodId]. */
    val foodId: String,
    /** Human-readable name, e.g. "Boiled Eggs" or "Veggies (mixed)". */
    val foodName: String,
    /** The size of one serving, in [servingUnit]. */
    val servingAmount: Float,
    /**
     * The unit one serving is measured in — a weight/volume unit like "g"
     * or "ml", or a count unit like "egg" or "pc". Quantities passed to
     * `createMeal` must be expressed in this same unit, not converted.
     */
    val servingUnit: String,
    /** Calories for one serving ([servingAmount] [servingUnit]). */
    val caloriesPerServing: Float,
    /** Protein in grams for one serving. */
    val proteinPerServing: Float,
    /** Carbohydrates in grams for one serving. */
    val carbsPerServing: Float,
    /** Fat in grams for one serving. */
    val fatsPerServing: Float,
    /** True if this food was added by the user rather than the built-in catalog. */
    val isCustomFood: Boolean,
)
