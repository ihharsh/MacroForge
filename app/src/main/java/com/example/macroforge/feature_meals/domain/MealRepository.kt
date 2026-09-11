package com.example.macroforge.feature_meals.domain

import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealTag
import kotlinx.coroutines.flow.Flow

// feature_meals/domain/MealRepository.kt
interface MealRepository {
    fun getAllMeals(): Flow<List<Meal>>
    suspend fun getMeal(mealId: String): Meal
    suspend fun saveMeal(meal: Meal)
    suspend fun deleteMeal(mealId: String)
    fun searchMeals(query: String, tag: MealTag?): Flow<List<Meal>>
}
