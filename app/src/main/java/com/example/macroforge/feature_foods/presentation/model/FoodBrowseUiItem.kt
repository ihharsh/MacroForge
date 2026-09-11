package com.example.macroforge.feature_foods.presentation.model

data class FoodBrowseUiItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val calories: Int,
    val protein: Float,
    val isCustom: Boolean
)
