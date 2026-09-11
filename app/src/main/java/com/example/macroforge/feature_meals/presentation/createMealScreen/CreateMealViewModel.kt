package com.example.macroforge.feature_meals.presentation.createMealScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.data.local.FoodSeeder
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealFoodEntry
import com.example.macroforge.feature_meals.domain.model.MealTag
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import com.example.macroforge.feature_meals.domain.usecase.MacroTotals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Single UI state for the whole screen instead of one StateFlow per field.
data class CreateMealUiState(
    val mealName: String = "",
    val selectedTag: MealTag = MealTag.BREAKFAST,
    val searchQuery: String = "",
    val searchResults: List<Food> = emptyList(),
    val selectedFoods: Map<Food, Float> = emptyMap(),
    val macroTotals: MacroTotals = MacroTotals(0f, 0f, 0f, 0f),
    val canSave: Boolean = false,
    val isSaving: Boolean = false,
    val saveErrorMessage: String? = null
)

// Everything the UI can ask this screen's ViewModel to do.
sealed interface CreateMealEvent {
    data class MealNameChanged(val value: String) : CreateMealEvent
    data class TagSelected(val tag: MealTag) : CreateMealEvent
    data class SearchQueryChanged(val value: String) : CreateMealEvent
    data object ClearSearch : CreateMealEvent
    data class AddFood(val food: Food, val quantity: Float) : CreateMealEvent
    data class QuantityChanged(val food: Food, val quantity: Float) : CreateMealEvent
    data class RemoveFood(val food: Food) : CreateMealEvent
    data object Save : CreateMealEvent
}

// One-time effects the UI should react to exactly once (navigation).
sealed interface CreateMealEffect {
    data object MealSaved : CreateMealEffect
}

// Raw, directly-editable fields — kept separate from CreateMealUiState because
// searchResults/macroTotals/canSave are *derived*, not user-entered.
private data class CreateMealFormState(
    val mealName: String = "",
    val selectedTag: MealTag = MealTag.BREAKFAST,
    val searchQuery: String = "",
    val selectedFoods: Map<Food, Float> = emptyMap(),
    val isSaving: Boolean = false,
    val saveErrorMessage: String? = null
)

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

    private val _formState = MutableStateFlow(CreateMealFormState())

    private val _effects = MutableSharedFlow<CreateMealEffect>()
    val effects: SharedFlow<CreateMealEffect> = _effects.asSharedFlow()

    private val searchResults: Flow<List<Food>> = _formState
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList()) else foodRepository.searchFoods(query)
        }

    val uiState: StateFlow<CreateMealUiState> = combine(_formState, searchResults) { form, results ->
        val macros = calculateMacros(
            foods = form.selectedFoods.keys.toList(),
            quantities = form.selectedFoods.entries.associate { it.key.foodId to it.value }
        )
        CreateMealUiState(
            mealName = form.mealName,
            selectedTag = form.selectedTag,
            searchQuery = form.searchQuery,
            searchResults = results,
            selectedFoods = form.selectedFoods,
            macroTotals = macros,
            canSave = form.mealName.isNotBlank() && form.selectedFoods.isNotEmpty(),
            isSaving = form.isSaving,
            saveErrorMessage = form.saveErrorMessage
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateMealUiState())

    fun onEvent(event: CreateMealEvent) {
        when (event) {
            is CreateMealEvent.MealNameChanged -> _formState.update { it.copy(mealName = event.value) }
            is CreateMealEvent.TagSelected -> _formState.update { it.copy(selectedTag = event.tag) }
            is CreateMealEvent.SearchQueryChanged -> _formState.update { it.copy(searchQuery = event.value) }
            CreateMealEvent.ClearSearch -> _formState.update { it.copy(searchQuery = "") }
            is CreateMealEvent.AddFood -> _formState.update {
                it.copy(
                    selectedFoods = it.selectedFoods + (event.food to event.quantity),
                    searchQuery = ""
                )
            }
            is CreateMealEvent.QuantityChanged -> _formState.update {
                it.copy(selectedFoods = it.selectedFoods + (event.food to event.quantity))
            }
            is CreateMealEvent.RemoveFood -> _formState.update {
                it.copy(selectedFoods = it.selectedFoods - event.food)
            }
            CreateMealEvent.Save -> saveMeal()
        }
    }

    private fun saveMeal() {
        val form = _formState.value
        val name = form.mealName.trim()
        if (name.isBlank() || form.selectedFoods.isEmpty()) return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            val now = System.currentTimeMillis()
            val meal = Meal(
                mealId = UUID.randomUUID().toString(),
                mealName = name,
                mealRecipe = null,
                mealTag = form.selectedTag,
                ownerId = "local_user",
                createdAt = now,
                updatedAt = now,
                syncStatus = "PENDING",
                entries = form.selectedFoods.map { (food, quantity) -> MealFoodEntry(food, quantity) }
            )
            try {
                mealRepository.saveMeal(meal)
                _formState.update { it.copy(isSaving = false) }
                _effects.emit(CreateMealEffect.MealSaved)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _formState.update {
                    it.copy(isSaving = false, saveErrorMessage = error.message ?: "Couldn't save meal. Please try again.")
                }
            }
        }
    }
}
