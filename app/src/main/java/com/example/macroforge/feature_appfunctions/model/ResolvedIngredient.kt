package com.example.macroforge.feature_appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

/** One ingredient as it was actually resolved and saved within a meal. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ResolvedIngredient(
    /** The catalog foodId this ingredient resolved to. */
    val foodId: String,
    /** The catalog food's display name. */
    val foodName: String,
    /** The quantity saved, in [unit]. */
    val quantity: Float,
    /** The unit [quantity] is expressed in (matches the food's servingUnit). */
    val unit: String,
)
