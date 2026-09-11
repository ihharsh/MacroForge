package com.example.macroforge.feature_foods.presentation.addFoodScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.macroforge.core.domain.UiState

@Composable
fun AddFoodRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddFoodViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val unitType by viewModel.unitType.collectAsStateWithLifecycle()
    val baseNumberText by viewModel.baseNumberText.collectAsStateWithLifecycle()
    val caloriesText by viewModel.caloriesText.collectAsStateWithLifecycle()
    val proteinText by viewModel.proteinText.collectAsStateWithLifecycle()
    val carbsText by viewModel.carbsText.collectAsStateWithLifecycle()
    val fatsText by viewModel.fatsText.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val isSaving = saveState is UiState.Loading
    val saveErrorMessage = (saveState as? UiState.Error)?.message

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect {
            onNavigateBack()
        }
    }

    AddFoodScreen(
        name = name,
        onNameChanged = viewModel::onNameChanged,
        unitType = unitType,
        onUnitTypeChanged = viewModel::onUnitTypeChanged,
        baseNumberText = baseNumberText,
        onBaseNumberChanged = viewModel::onBaseNumberChanged,
        caloriesText = caloriesText,
        onCaloriesChanged = viewModel::onCaloriesChanged,
        proteinText = proteinText,
        onProteinChanged = viewModel::onProteinChanged,
        carbsText = carbsText,
        onCarbsChanged = viewModel::onCarbsChanged,
        fatsText = fatsText,
        onFatsChanged = viewModel::onFatsChanged,
        canSave = canSave,
        isSaving = isSaving,
        saveErrorMessage = saveErrorMessage,
        onBack = onNavigateBack,
        onSave = viewModel::saveFood
    )
}
