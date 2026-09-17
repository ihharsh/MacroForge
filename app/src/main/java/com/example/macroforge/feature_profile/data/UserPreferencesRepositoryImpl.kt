package com.example.macroforge.feature_profile.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.macroforge.feature_profile.domain.UserPreferencesRepository
import com.example.macroforge.feature_profile.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// feature_profile/data/UserPreferencesRepositoryImpl.kt
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object Keys {
        val CALORIE_GOAL = intPreferencesKey("daily_calorie_goal")
        val PROTEIN_GOAL = intPreferencesKey("protein_goal_grams")
        val CARBS_GOAL = intPreferencesKey("carbs_goal_grams")
        val FATS_GOAL = intPreferencesKey("fats_goal_grams")
    }

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            // DataStore throws IOException on read failure; fall back to
            // an empty snapshot (defaults apply below) rather than crashing.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            val defaults = UserPreferences()
            UserPreferences(
                dailyCalorieGoal = prefs[Keys.CALORIE_GOAL] ?: defaults.dailyCalorieGoal,
                proteinGoalGrams = prefs[Keys.PROTEIN_GOAL] ?: defaults.proteinGoalGrams,
                carbsGoalGrams = prefs[Keys.CARBS_GOAL] ?: defaults.carbsGoalGrams,
                fatsGoalGrams = prefs[Keys.FATS_GOAL] ?: defaults.fatsGoalGrams
            )
        }

    override suspend fun updateDailyCalorieGoal(calories: Int) {
        dataStore.edit { it[Keys.CALORIE_GOAL] = calories }
    }

    override suspend fun updateMacroGoals(proteinGrams: Int, carbsGrams: Int, fatsGrams: Int) {
        dataStore.edit {
            it[Keys.PROTEIN_GOAL] = proteinGrams
            it[Keys.CARBS_GOAL] = carbsGrams
            it[Keys.FATS_GOAL] = fatsGrams
        }
    }
}
