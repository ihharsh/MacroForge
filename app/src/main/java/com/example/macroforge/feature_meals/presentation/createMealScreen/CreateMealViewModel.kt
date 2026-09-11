package com.example.macroforge.feature_meals.presentation.createMealScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.data.local.FoodSeeder
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealFoodEntry
import com.example.macroforge.feature_meals.domain.model.MealTag
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import com.example.macroforge.feature_meals.domain.usecase.MacroTotals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState

    private val _mealName = MutableStateFlow("")
    val mealName: StateFlow<String> = _mealName

    private val _selectedTag = MutableStateFlow(MealTag.BREAKFAST)
    val selectedTag: StateFlow<MealTag> = _selectedTag

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    val searchResults: StateFlow<List<Food>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList()) else foodRepository.searchFoods(query)
        }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    private val _selectedFoods = MutableStateFlow<Map<Food, Float>>(emptyMap())
    val selectedFoods: StateFlow<Map<Food, Float>> = _selectedFoods

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

    // A meal needs a name and at least one food before it can be saved.
    val canSave: StateFlow<Boolean> = combine(_mealName, _selectedFoods) { name, foods ->
        name.isNotBlank() && foods.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), false)

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun onMealNameChanged(name: String) { _mealName.value = name }

    fun onTagSelected(tag: MealTag) { _selectedTag.value = tag }

    fun addFood(food: Food, quantity: Float) {
        _selectedFoods.value = _selectedFoods.value + (food to quantity)
    }

    fun updateQuantity(food: Food, quantity: Float) {
        _selectedFoods.value = _selectedFoods.value + (food to quantity)
    }

    fun removeFood(food: Food) {
        _selectedFoods.value = _selectedFoods.value - food
    }

    fun saveMeal() {
        val name = _mealName.value.trim()
        val foods = _selectedFoods.value
        if (name.isBlank() || foods.isEmpty()) return

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val now = System.currentTimeMillis()
            val meal = Meal(
                mealId = UUID.randomUUID().toString(),
                mealName = name,
                mealRecipe = null,
                mealTag = _selectedTag.value,
                ownerId = "local_user",
                createdAt = now,
                updatedAt = now,
                syncStatus = "PENDING",
                entries = foods.map { (food, quantity) -> MealFoodEntry(food, quantity) }
            )
            try {
                mealRepository.saveMeal(meal)
                _saveState.value = UiState.Success(Unit)
                _saveSuccess.emit(Unit)
            } catch (cancellation: kotlinx.coroutines.CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _saveState.value = UiState.Error(error.message ?: "Couldn't save meal. Please try again.")
            }
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
}
