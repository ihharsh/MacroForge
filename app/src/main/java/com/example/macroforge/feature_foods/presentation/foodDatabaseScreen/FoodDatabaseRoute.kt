package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_foods.presentation.model.FoodBrowseUiItem

@Composable
fun FoodDatabaseRoute(
    onNavigateToAddFood: () -> Unit,
    viewModel: FoodDatabaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val foodUiState = remember(uiState.foodsState) {
        when (val state = uiState.foodsState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(state.message)
            is UiState.Success -> UiState.Success(
                state.data.map { food ->
                    FoodBrowseUiItem(
                        id = food.foodId,
                        name = food.foodName,
                        subtitle = "per ${food.baseNumber.toInt()}${food.unitType}",
                        calories = food.calories.toInt(),
                        protein = food.protein,
                        isCustom = food.isCustom
                    )
                }
            )
        }
    }

    FoodDatabaseScreen(
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = { viewModel.onEvent(FoodDatabaseEvent.SearchQueryChanged(it)) },
        foodsState = foodUiState,
        onAddFoodClicked = onNavigateToAddFood
    )
}
