package com.example.macroforge.feature_foods.presentation.addFoodScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddFoodRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddFoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddFoodEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AddFoodScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onNavigateBack
    )
}
