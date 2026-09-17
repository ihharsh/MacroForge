package com.example.macroforge.feature_profile.domain.model

// Domain model for user-editable goals — framework-agnostic, no DataStore types.
data class UserPreferences(
    val dailyCalorieGoal: Int = 2000,
    val proteinGoalGrams: Int = 150,
    val carbsGoalGrams: Int = 200,
    val fatsGoalGrams: Int = 65
)
