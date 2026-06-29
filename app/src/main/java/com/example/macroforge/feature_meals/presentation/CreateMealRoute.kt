package com.example.macroforge.feature_meals.presentation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.core.data.local.entity.MealTag

@Composable
fun CreateMealRoute(
    viewModel: CreateMealViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedFoods by viewModel.selectedFoods.collectAsState()
    val macros by viewModel.macroTotals.collectAsState()

    CreateMealScreen(
        searchQuery = searchQuery,
        searchResults = searchResults,
        selectedFoods = selectedFoods,
        macros = macros,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onAddFood = { food ->
            viewModel.addFood(food, food.baseNumber)
            viewModel.removeSearchQuery()

        },
        onUpdateQuantity = viewModel::updateQuantity,
        onRemoveFood = viewModel::removeFood,
        onSave = {
            viewModel.saveMeal(
                name = "New Meal",
                tag = MealTag.BREAKFAST,
                onSaved = {}
            )
        }
    )
}