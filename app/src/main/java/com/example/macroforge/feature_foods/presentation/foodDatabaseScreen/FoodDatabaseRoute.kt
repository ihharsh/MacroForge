package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.example.macroforge.feature_foods.presentation.model.FoodBrowseUiItem
import kotlinx.coroutines.flow.map

@Composable
fun FoodDatabaseRoute(
    onNavigateToAddFood: () -> Unit,
    viewModel: FoodDatabaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Map domain PagingData<Food> -> PagingData<FoodBrowseUiItem>. Mapping is
    // applied downstream of the ViewModel's cachedIn(), which is fine here
    // since this screen only ever has one collector.
    val foods = remember(viewModel) {
        viewModel.pagedFoods.map { pagingData ->
            pagingData.map { food ->
                FoodBrowseUiItem(
                    id = food.foodId,
                    name = food.foodName,
                    subtitle = "per ${food.baseNumber.toInt()}${food.unitType}",
                    calories = food.calories.toInt(),
                    protein = food.protein,
                    isCustom = food.isCustom
                )
            }
        }
    }.collectAsLazyPagingItems()

    FoodDatabaseScreen(
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = { viewModel.onEvent(FoodDatabaseEvent.SearchQueryChanged(it)) },
        foods = foods,
        onAddFoodClicked = onNavigateToAddFood
    )
}
