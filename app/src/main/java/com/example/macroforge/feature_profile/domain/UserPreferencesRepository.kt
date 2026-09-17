package com.example.macroforge.feature_profile.domain

import com.example.macroforge.feature_profile.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

// feature_profile/domain/UserPreferencesRepository.kt
interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateDailyCalorieGoal(calories: Int)
    suspend fun updateMacroGoals(proteinGrams: Int, carbsGrams: Int, fatsGrams: Int)
}
