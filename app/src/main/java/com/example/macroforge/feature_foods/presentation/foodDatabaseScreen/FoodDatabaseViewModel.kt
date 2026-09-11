package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Single UI state for the whole screen instead of separate StateFlows per field.
data class FoodDatabaseUiState(
    val searchQuery: String = "",
    val foodsState: UiState<List<Food>> = UiState.Loading
)

// Everything the UI can ask this screen's ViewModel to do.
sealed interface FoodDatabaseEvent {
    data class SearchQueryChanged(val value: String) : FoodDatabaseEvent
}

@HiltViewModel
class FoodDatabaseViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val foodsState: Flow<UiState<List<Food>>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) foodRepository.getAllFoods() else foodRepository.searchFoods(query)
        }
        .map<List<Food>, UiState<List<Food>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { error -> emit(UiState.Error(error.message ?: "Couldn't load foods.")) }

    val uiState: StateFlow<FoodDatabaseUiState> = combine(_searchQuery, foodsState) { query, foods ->
        FoodDatabaseUiState(searchQuery = query, foodsState = foods)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FoodDatabaseUiState())

    fun onEvent(event: FoodDatabaseEvent) {
        when (event) {
            is FoodDatabaseEvent.SearchQueryChanged -> _searchQuery.value = event.value
        }
    }
}
