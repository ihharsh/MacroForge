package com.example.macroforge.feature_foods.presentation.addFoodScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Single source of truth for the whole form — one state object instead of a
// MutableStateFlow per field. All fields default to what the form should show
// on first open.
data class AddFoodUiState(
    val name: String = "",
    val unitType: String = "g",
    val baseNumberText: String = "100",
    val caloriesText: String = "",
    val proteinText: String = "",
    val carbsText: String = "",
    val fatsText: String = "",
    val isSaving: Boolean = false,
    val saveErrorMessage: String? = null
) {
    val canSave: Boolean
        get() = name.isNotBlank() &&
            (baseNumberText.toFloatOrNull() ?: 0f) > 0f &&
            caloriesText.toFloatOrNull() != null
}

// Everything the UI can ask the ViewModel to do — one entry point (onEvent)
// instead of one method per field.
sealed interface AddFoodEvent {
    data class NameChanged(val value: String) : AddFoodEvent
    data class UnitTypeChanged(val value: String) : AddFoodEvent
    data class BaseNumberChanged(val value: String) : AddFoodEvent
    data class CaloriesChanged(val value: String) : AddFoodEvent
    data class ProteinChanged(val value: String) : AddFoodEvent
    data class CarbsChanged(val value: String) : AddFoodEvent
    data class FatsChanged(val value: String) : AddFoodEvent
    data object Save : AddFoodEvent
}

// One-time effects the UI should react to exactly once (navigation), kept
// separate from persistent UiState per the SharedFlow-for-one-time-events rule.
sealed interface AddFoodEffect {
    data object NavigateBack : AddFoodEffect
}

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AddFoodEffect>()
    val effects: SharedFlow<AddFoodEffect> = _effects.asSharedFlow()

    fun onEvent(event: AddFoodEvent) {
        when (event) {
            is AddFoodEvent.NameChanged -> _uiState.update { it.copy(name = event.value) }
            is AddFoodEvent.UnitTypeChanged -> _uiState.update { it.copy(unitType = event.value) }
            is AddFoodEvent.BaseNumberChanged -> _uiState.update { it.copy(baseNumberText = event.value) }
            is AddFoodEvent.CaloriesChanged -> _uiState.update { it.copy(caloriesText = event.value) }
            is AddFoodEvent.ProteinChanged -> _uiState.update { it.copy(proteinText = event.value) }
            is AddFoodEvent.CarbsChanged -> _uiState.update { it.copy(carbsText = event.value) }
            is AddFoodEvent.FatsChanged -> _uiState.update { it.copy(fatsText = event.value) }
            AddFoodEvent.Save -> saveFood()
        }
    }

    private fun saveFood() {
        val state = _uiState.value
        val name = state.name.trim()
        val baseNumber = state.baseNumberText.toFloatOrNull()
        val calories = state.caloriesText.toFloatOrNull()
        if (name.isBlank() || baseNumber == null || baseNumber <= 0f || calories == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            val food = Food(
                foodId = UUID.randomUUID().toString(),
                foodName = name,
                baseNumber = baseNumber,
                unitType = state.unitType.ifBlank { "g" },
                calories = calories,
                carbs = state.carbsText.toFloatOrNull() ?: 0f,
                protein = state.proteinText.toFloatOrNull() ?: 0f,
                fats = state.fatsText.toFloatOrNull() ?: 0f,
                isCustom = true,
                ownerId = "local_user"
            )
            try {
                foodRepository.addCustomFood(food)
                _uiState.update { it.copy(isSaving = false) }
                _effects.emit(AddFoodEffect.NavigateBack)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, saveErrorMessage = error.message ?: "Couldn't save food. Please try again.")
                }
            }
        }
    }
}
