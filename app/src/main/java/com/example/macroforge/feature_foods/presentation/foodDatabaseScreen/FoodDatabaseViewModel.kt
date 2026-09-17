package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Text-field state only. The paged food list itself is a PagingData stream —
// Paging3 has its own load-state model (surfaced via LazyPagingItems in
// Compose), so it deliberately isn't folded into this state object alongside
// UiState<T>-style screens; forcing it in would fight Paging's own diffing.
data class FoodDatabaseUiState(
    val searchQuery: String = ""
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

    val uiState: StateFlow<FoodDatabaseUiState> = _searchQuery
        .map { FoodDatabaseUiState(searchQuery = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FoodDatabaseUiState())

    val pagedFoods: Flow<PagingData<Food>> = _searchQuery
        .debounce(200)
        .distinctUntilChanged()
        .flatMapLatest { query -> foodRepository.getPagedFoods(query) }
        .cachedIn(viewModelScope)

    fun onEvent(event: FoodDatabaseEvent) {
        when (event) {
            is FoodDatabaseEvent.SearchQueryChanged -> _searchQuery.value = event.value
        }
    }
}
