package com.example.macroforge.feature_foods.domain

import com.example.macroforge.feature_foods.domain.model.Food
import kotlinx.coroutines.flow.Flow

// feature_foods/domain/FoodRepository.kt
interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>
    fun searchFoods(query: String): Flow<List<Food>>
    suspend fun addCustomFood(food: Food)
    suspend fun seedMasterFoodsIfEmpty(foods: List<Food>)
}
