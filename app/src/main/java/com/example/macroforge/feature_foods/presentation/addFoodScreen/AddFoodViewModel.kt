package com.example.macroforge.feature_foods.presentation.addFoodScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _unitType = MutableStateFlow("g")
    val unitType: StateFlow<String> = _unitType

    private val _baseNumberText = MutableStateFlow("100")
    val baseNumberText: StateFlow<String> = _baseNumberText

    private val _caloriesText = MutableStateFlow("")
    val caloriesText: StateFlow<String> = _caloriesText

    private val _proteinText = MutableStateFlow("")
    val proteinText: StateFlow<String> = _proteinText

    private val _carbsText = MutableStateFlow("")
    val carbsText: StateFlow<String> = _carbsText

    private val _fatsText = MutableStateFlow("")
    val fatsText: StateFlow<String> = _fatsText

    val canSave: StateFlow<Boolean> = combine(_name, _baseNumberText, _caloriesText) { name, baseNumber, calories ->
        name.isNotBlank() && (baseNumber.toFloatOrNull() ?: 0f) > 0f && calories.toFloatOrNull() != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState

    private val _saveSuccess = MutableSharedFlow<Unit>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    fun onNameChanged(value: String) { _name.value = value }
    fun onUnitTypeChanged(value: String) { _unitType.value = value }
    fun onBaseNumberChanged(value: String) { _baseNumberText.value = value }
    fun onCaloriesChanged(value: String) { _caloriesText.value = value }
    fun onProteinChanged(value: String) { _proteinText.value = value }
    fun onCarbsChanged(value: String) { _carbsText.value = value }
    fun onFatsChanged(value: String) { _fatsText.value = value }

    fun saveFood() {
        val name = _name.value.trim()
        val baseNumber = _baseNumberText.value.toFloatOrNull()
        val calories = _caloriesText.value.toFloatOrNull()
        if (name.isBlank() || baseNumber == null || baseNumber <= 0f || calories == null) return

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val food = Food(
                foodId = UUID.randomUUID().toString(),
                foodName = name,
                baseNumber = baseNumber,
                unitType = _unitType.value.ifBlank { "g" },
                calories = calories,
                carbs = _carbsText.value.toFloatOrNull() ?: 0f,
                protein = _proteinText.value.toFloatOrNull() ?: 0f,
                fats = _fatsText.value.toFloatOrNull() ?: 0f,
                isCustom = true,
                ownerId = "local_user"
            )
            try {
                foodRepository.addCustomFood(food)
                _saveState.value = UiState.Success(Unit)
                _saveSuccess.emit(Unit)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _saveState.value = UiState.Error(error.message ?: "Couldn't save food. Please try again.")
            }
        }
    }
}
