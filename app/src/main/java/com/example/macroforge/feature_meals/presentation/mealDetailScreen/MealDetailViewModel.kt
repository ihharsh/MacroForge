package com.example.macroforge.feature_meals.presentation.mealDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.example.macroforge.feature_meals.presentation.util.displayLabel
import com.example.macroforge.feature_meals.presentation.util.toFormattedTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MealDetailUiState {
    data object Loading : MealDetailUiState
    data class Loaded(val meal: MealDetailUiModel) : MealDetailUiState
    data object NotFound : MealDetailUiState
}

data class MealDetailUiModel(
    val mealName: String,
    val tagLabel: String,
    val time: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFats: Float,
    val foods: List<FoodUiItem>
)

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealRepository: MealRepository,
    private val calculateMacros: CalculateMealMacrosUseCase
) : ViewModel() {

    private val mealId: String = checkNotNull(savedStateHandle["mealId"])

    private val _uiState = MutableStateFlow<MealDetailUiState>(MealDetailUiState.Loading)
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val meal = runCatching { mealRepository.getMeal(mealId) }.getOrNull()
            _uiState.value = if (meal == null) {
                MealDetailUiState.NotFound
            } else {
                val totals = calculateMacros(meal.foods, meal.quantitiesByFoodId)
                val foodItems = meal.entries.map { entry ->
                    val ratio = entry.quantity / entry.food.baseNumber
                    FoodUiItem(
                        id = entry.food.foodId,
                        name = entry.food.foodName,
                        subtitle = "per ${entry.food.baseNumber.toInt()}${entry.food.unitType}",
                        quantity = entry.quantity,
                        unit = entry.food.unitType,
                        calories = entry.food.calories * ratio,
                        protein = entry.food.protein * ratio,
                        carbs = entry.food.carbs * ratio,
                        fats = entry.food.fats * ratio
                    )
                }
                MealDetailUiState.Loaded(
                    MealDetailUiModel(
                        mealName = meal.mealName,
                        tagLabel = meal.mealTag.displayLabel(),
                        time = meal.createdAt.toFormattedTime(),
                        totalCalories = totals.calories,
                        totalProtein = totals.protein,
                        totalCarbs = totals.carbs,
                        totalFats = totals.fats,
                        foods = foodItems
                    )
                )
            }
        }
    }
}
