package com.example.macroforge.feature_meals.presentation.model

// ---------- UI models ----------
data class FoodUiItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val quantity: Float,
    val unit: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fats: Float
)

data class FoodSearchResultUiItem(
    val id: String,
    val name: String,
    val subtitle: String,     // e.g. "per 150g"
    val calories: Int,
    val protein: Float
)