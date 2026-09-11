package com.example.macroforge.feature_meals.presentation.savedMealScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.relation.MealWithFoods
import com.example.macroforge.feature_meals.domain.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Placeholder domain type until MealRepository is injected
// Replace with real MealWithFoods from your Room relation once wired
data class MealWithFoodsPlaceholder(
    val meal: Any,
    val foods: List<Any>,
    val crossRefs: List<Any>
)

@HiltViewModel
class SavedMealsViewModel @Inject constructor(
     private val mealRepository: MealRepository
) : ViewModel() {

    // ── Search ──────────────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // ── Tag filter ───────────────────────────────────────────────────────────
    private val _selectedTag = MutableStateFlow(MealTagUi.ALL)
    val selectedTag: StateFlow<MealTagUi> = _selectedTag.asStateFlow()

    // ── Meals ────────────────────────────────────────────────────────────────
    // TODO: replace with real repo call:
//     private val _meals = combine(_searchQuery, _selectedTag) { query, tag ->
//         mealRepository.searchMeals(query, tag.name)
//     }.flatMapLatest { it }
//      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _meals = combine(_searchQuery, _selectedTag) {
        mealRepository.getAllMeals()
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    //private val _meals = MutableStateFlow<List<MealWithFoodsPlaceholder>>(emptyList())
     val meals: StateFlow<List<MealWithFoods>> = _meals
    //val meals: StateFlow<List<MealWithFoodsPlaceholder>> = _meals.asStateFlow()

    // ── Total kcal today ─────────────────────────────────────────────────────
    // TODO: derive from today's meals once repo is wired
    private val _totalKcalToday = MutableStateFlow(0)
    val totalKcalToday: StateFlow<Int> = _totalKcalToday.asStateFlow()

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
}