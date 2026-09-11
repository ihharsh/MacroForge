package com.example.macroforge.feature_meals.presentation.savedMealScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.usecase.CalculateDailyTotalsUseCase
import com.example.macroforge.feature_meals.presentation.util.toDomainTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMealsViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val calculateDailyTotals: CalculateDailyTotalsUseCase
) : ViewModel() {

    // ── Search ──────────────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // ── Tag filter ───────────────────────────────────────────────────────────
    private val _selectedTag = MutableStateFlow(MealTagUi.ALL)
    val selectedTag: StateFlow<MealTagUi> = _selectedTag.asStateFlow()

    // ── Meals (filtered by the current search text + tag) ─────────────────────
    val mealsState: StateFlow<UiState<List<Meal>>> = combine(_searchQuery, _selectedTag) { query, tag ->
        mealRepository.searchMeals(query, tag.toDomainTag())
    }.flatMapLatest { it }
        .map<List<Meal>, UiState<List<Meal>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { error -> emit(UiState.Error(error.message ?: "Couldn't load meals.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // ── Total kcal today — independent of the search/tag filter above ─────────
    private val allMeals: StateFlow<List<Meal>> = mealRepository.getAllMeals()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalKcalToday: StateFlow<Int> = allMeals
        .map { calculateDailyTotals(it).calories.toInt() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ── Event handlers ───────────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchToggled() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun onCancelSearch() {
        _isSearchActive.value = false
        _searchQuery.value = ""
    }

    fun onTagSelected(tag: MealTagUi) {
        _selectedTag.value = tag
    }

    fun onDeleteMeal(mealId: String) {
        viewModelScope.launch {
            mealRepository.deleteMeal(mealId)
        }
    }
}
