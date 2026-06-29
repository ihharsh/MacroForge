package com.example.macroforge.feature_meals.domain

import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.core.data.local.relation.MealWithFoods
import kotlinx.coroutines.flow.Flow

// feature_meals/domain/MealRepository.kt
interface MealRepository {
    fun getAllMeals(): Flow<List<MealWithFoods>>
    suspend fun getMeal(mealId: String): MealWithFoods
    suspend fun saveMeal(meal: MealEntity, foodEntries: List<MealFoodCrossRef>)
    fun searchMeals(query: String, tag: String): Flow<List<MealEntity>>
}