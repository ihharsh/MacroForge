package com.example.macroforge.feature_meals.presentation.savedMealScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.usecase.CalculateDailyTotalsUseCase
import com.example.macroforge.feature_meals.presentation.util.toDomainTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Single UI state for the whole screen instead of one StateFlow per field.
data class SavedMealsUiState(
    val mealsState: UiState<List<Meal>> = UiState.Loading,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedTag: MealTagUi = MealTagUi.ALL,
    val totalKcalToday: Int = 0
)

// Everything the UI can ask this screen's ViewModel to do.
sealed interface SavedMealsEvent {
    data class SearchQueryChanged(val value: String) : SavedMealsEvent
    data object SearchToggled : SavedMealsEvent
    data object CancelSearch : SavedMealsEvent
    data class TagSelected(val tag: MealTagUi) : SavedMealsEvent
    data class DeleteMeal(val mealId: String) : SavedMealsEvent
}

@HiltViewModel
class SavedMealsViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val calculateDailyTotals: CalculateDailyTotalsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _selectedTag = MutableStateFlow(MealTagUi.ALL)

    // Meals filtered by the current search text + tag.
    private val mealsState: Flow<UiState<List<Meal>>> = combine(_searchQuery, _selectedTag) { query, tag ->
        mealRepository.searchMeals(query, tag.toDomainTag())
    }.flatMapLatest { it }
        .map<List<Meal>, UiState<List<Meal>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { error -> emit(UiState.Error(error.message ?: "Couldn't load meals.")) }

    // Total kcal today — independent of the search/tag filter above.
    private val totalKcalToday: Flow<Int> = mealRepository.getAllMeals()
        .catch { emit(emptyList()) }
        .map { calculateDailyTotals(it).calories.toInt() }

    val uiState: StateFlow<SavedMealsUiState> = combine(
        mealsState, _searchQuery, _isSearchActive, _selectedTag, totalKcalToday
    ) { meals, query, isActive, tag, kcalToday ->
        SavedMealsUiState(
            mealsState = meals,
            searchQuery = query,
            isSearchActive = isActive,
            selectedTag = tag,
            totalKcalToday = kcalToday
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedMealsUiState())

    fun onEvent(event: SavedMealsEvent) {
        when (event) {
            is SavedMealsEvent.SearchQueryChanged -> _searchQuery.value = event.value
            SavedMealsEvent.SearchToggled -> {
                _isSearchActive.value = !_isSearchActive.value
                if (!_isSearchActive.value) _searchQuery.value = ""
            }
            SavedMealsEvent.CancelSearch -> {
                _isSearchActive.value = false
                _searchQuery.value = ""
            }
            is SavedMealsEvent.TagSelected -> _selectedTag.value = event.tag
            is SavedMealsEvent.DeleteMeal -> viewModelScope.launch {
                mealRepository.deleteMeal(event.mealId)
            }
        }
    }
}
