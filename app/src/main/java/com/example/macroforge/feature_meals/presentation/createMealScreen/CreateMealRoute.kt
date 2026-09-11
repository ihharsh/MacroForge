package com.example.macroforge.feature_meals.presentation.createMealScreen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.macroforge.core.data.local.entity.MealTag
import com.example.macroforge.feature_meals.presentation.model.FoodSearchResultUiItem
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.yourname.macroforge.feature_meals.presentation.CreateMealScreen

@Composable
fun CreateMealRoute(
    onNavigateBack: () -> Unit,
    onNavigateToSaveMeal: () -> Unit,
    viewModel: CreateMealViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedFoods by viewModel.selectedFoods.collectAsState()
    val macros by viewModel.macroTotals.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect {
            onNavigateToSaveMeal()
        }
    }

    // Map domain FoodEntity -> UI search result model
    val searchResultUiItems = remember(searchResults) {
        searchResults.map { food ->
            FoodSearchResultUiItem(
                id = food.foodId,
                name = food.foodName,
                subtitle = "per ${food.baseNumber.toInt()}${food.unitType}",
                calories = food.calories.toInt(),
                protein = food.protein
            )
        }
    }

    // Map domain (FoodEntity + quantity) -> UI list item, scaling macros by quantity
    val foodUiItems = remember(selectedFoods) {
        selectedFoods.map { (food, qty) ->
            val ratio = qty / food.baseNumber
            FoodUiItem(
                id = food.foodId,
                name = food.foodName,
                subtitle = "per ${food.baseNumber.toInt()}${food.unitType}",
                quantity = qty,
                unit = food.unitType,
                calories = food.calories * ratio,
                protein = food.protein * ratio,
                carbs = food.carbs * ratio,
                fats = food.fats * ratio
            )
        }
    }

    // Lookup to map a UI item back to its real FoodEntity for ViewModel calls
    val foodById = remember(selectedFoods) {
        selectedFoods.keys.associateBy { it.foodId }
    }
    val searchResultById = remember(searchResults) {
        searchResults.associateBy { it.foodId }
    }

    CreateMealScreen(
        searchQuery = searchQuery,
        searchResults = searchResultUiItems,
        foods = foodUiItems,
        totalCalories = macros.calories,
        totalProtein = macros.protein,
        totalCarbs = macros.carbs,
        totalFats = macros.fats,
        onBack = onNavigateBack,
        onSave = { viewModel.saveMeal("meal123", MealTag.POST_WORKOUT) },     // top-right "Save" -> go to Save Meal screen
        onSaveMeal = { viewModel.saveMeal("meal123", MealTag.POST_WORKOUT) }, //bottom "Save Meal" button -> same destination
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearch = viewModel::clearSearchQuery,
        onAddFood = { uiResult ->
            searchResultById[uiResult.id]?.let { foodEntity ->
                viewModel.addFood(foodEntity, foodEntity.baseNumber)
                viewModel.clearSearchQuery()
            }
        },
        onQuantityChanged = { uiItem, newQty ->
            foodById[uiItem.id]?.let { foodEntity ->
                viewModel.updateQuantity(foodEntity, newQty)
            }
        },
        onRemoveFood = { uiItem ->
            foodById[uiItem.id]?.let { foodEntity ->
                viewModel.removeFood(foodEntity)
            }
        }
    )
}
