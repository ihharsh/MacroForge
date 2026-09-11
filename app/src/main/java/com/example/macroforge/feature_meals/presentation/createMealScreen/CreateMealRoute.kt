package com.example.macroforge.feature_meals.presentation.createMealScreen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.macroforge.feature_meals.presentation.model.FoodSearchResultUiItem
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem

@Composable
fun CreateMealRoute(
    onNavigateBack: () -> Unit,
    onNavigateToSaveMeal: () -> Unit,
    viewModel: CreateMealViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CreateMealEffect.MealSaved -> onNavigateToSaveMeal()
            }
        }
    }

    // Map domain Food -> UI search result model
    val searchResultUiItems = remember(uiState.searchResults) {
        uiState.searchResults.map { food ->
            FoodSearchResultUiItem(
                id = food.foodId,
                name = food.foodName,
                subtitle = "per ${food.baseNumber.toInt()}${food.unitType}",
                calories = food.calories.toInt(),
                protein = food.protein
            )
        }
    }

    // Map domain (Food + quantity) -> UI list item, scaling macros by quantity
    val foodUiItems = remember(uiState.selectedFoods) {
        uiState.selectedFoods.map { (food, qty) ->
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

    // Lookup to map a UI item back to its real Food for ViewModel calls
    val foodById = remember(uiState.selectedFoods) {
        uiState.selectedFoods.keys.associateBy { it.foodId }
    }
    val searchResultById = remember(uiState.searchResults) {
        uiState.searchResults.associateBy { it.foodId }
    }

    CreateMealScreen(
        searchQuery = uiState.searchQuery,
        searchResults = searchResultUiItems,
        foods = foodUiItems,
        totalCalories = uiState.macroTotals.calories,
        totalProtein = uiState.macroTotals.protein,
        totalCarbs = uiState.macroTotals.carbs,
        totalFats = uiState.macroTotals.fats,
        mealName = uiState.mealName,
        onMealNameChanged = { name -> viewModel.onEvent(CreateMealEvent.MealNameChanged(name)) },
        selectedTag = uiState.selectedTag,
        onTagSelected = { tag -> viewModel.onEvent(CreateMealEvent.TagSelected(tag)) },
        canSave = uiState.canSave,
        isSaving = uiState.isSaving,
        saveErrorMessage = uiState.saveErrorMessage,
        onBack = onNavigateBack,
        onSave = { viewModel.onEvent(CreateMealEvent.Save) },       // top-right "Save" -> go to Save Meal screen
        onSaveMeal = { viewModel.onEvent(CreateMealEvent.Save) },   // bottom "Save Meal" button -> same destination
        onSearchQueryChanged = { query -> viewModel.onEvent(CreateMealEvent.SearchQueryChanged(query)) },
        onClearSearch = { viewModel.onEvent(CreateMealEvent.ClearSearch) },
        onAddFood = { uiResult ->
            searchResultById[uiResult.id]?.let { food ->
                viewModel.onEvent(CreateMealEvent.AddFood(food, food.baseNumber))
            }
        },
        onQuantityChanged = { uiItem, newQty ->
            foodById[uiItem.id]?.let { food ->
                viewModel.onEvent(CreateMealEvent.QuantityChanged(food, newQty))
            }
        },
        onRemoveFood = { uiItem ->
            foodById[uiItem.id]?.let { food ->
                viewModel.onEvent(CreateMealEvent.RemoveFood(food))
            }
        }
    )
}
