package com.example.macroforge.feature_meals.presentation.createMealScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.data.local.FoodSeeder
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.core.data.local.entity.MealTag
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import com.example.macroforge.feature_meals.domain.usecase.MacroTotals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateMealViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val mealRepository: MealRepository,
    private val calculateMacros: CalculateMealMacrosUseCase,
    private val foodSeeder: FoodSeeder
) : ViewModel() {
    init {
        viewModelScope.launch { foodSeeder.seedIfNeeded() }
    }
    private val _saveSuccess = MutableSharedFlow<Unit>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    val searchResults: StateFlow<List<FoodEntity>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList()) else foodRepository.searchFoods(query)
        }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    private val _selectedFoods = MutableStateFlow<Map<FoodEntity, Float>>(emptyMap())
    val selectedFoods: StateFlow<Map<FoodEntity, Float>> = _selectedFoods

    val macroTotals: StateFlow<MacroTotals> = _selectedFoods
        .map { selected ->
            calculateMacros(
                foods = selected.keys.toList(),
                quantities = selected.entries.associate { it.key.foodId to it.value }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000),
            MacroTotals(0f, 0f, 0f, 0f)
        )

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun addFood(food: FoodEntity, quantity: Float) {
        _selectedFoods.value = _selectedFoods.value + (food to quantity)
    }

    fun updateQuantity(food: FoodEntity, quantity: Float) {
        _selectedFoods.value = _selectedFoods.value + (food to quantity)
    }

    fun removeFood(food: FoodEntity) {
        _selectedFoods.value = _selectedFoods.value - food
    }

    fun saveMeal(name: String, tag: MealTag) {
        viewModelScope.launch {
            val mealId = UUID.randomUUID().toString()
            val meal = MealEntity(mealId, name, null, tag, ownerId = "local_user")
            val crossRefs = _selectedFoods.value.map { (food, qty) ->
                MealFoodCrossRef(mealId, food.foodId, qty)
            }
            mealRepository.saveMeal(meal, crossRefs)
            _saveSuccess.emit(Unit)
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
}