package com.example.macroforge.feature_foods.domain.model

// Domain model for a food item — framework-agnostic (no Room/Compose/Firebase),
// so it can move into a shared/KMP module later without changes.
data class Food(
    val foodId: String,
    val foodName: String,
    val baseNumber: Float,
    val unitType: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fats: Float,
    val isCustom: Boolean = false,
    val ownerId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
