package com.example.macroforge.core.domain

// Generic async state wrapper for ViewModel-exposed data — framework-agnostic
// (no Android/Compose/Firebase types) so it stays portable. Screens branch on
// this instead of silently showing a blank list while data loads or fails.
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
