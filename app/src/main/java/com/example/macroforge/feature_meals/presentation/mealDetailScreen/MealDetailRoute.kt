package com.example.macroforge.feature_meals.presentation.mealDetailScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MealDetailRoute(
    onNavigateBack: () -> Unit,
    viewModel: MealDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MealDetailScreen(
        uiState = uiState,
        onBack = onNavigateBack
    )
}
