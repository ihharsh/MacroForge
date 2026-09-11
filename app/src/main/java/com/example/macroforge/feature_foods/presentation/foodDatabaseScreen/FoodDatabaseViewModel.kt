package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FoodDatabaseViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val foodsState: StateFlow<UiState<List<Food>>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) foodRepository.getAllFoods() else foodRepository.searchFoods(query)
        }
        .map<List<Food>, UiState<List<Food>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { error -> emit(UiState.Error(error.message ?: "Couldn't load foods.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
