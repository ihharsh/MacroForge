package com.example.macroforge.feature_foods.domain

import com.example.macroforge.core.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

// feature_foods/domain/FoodRepository.kt
interface FoodRepository {
    fun getAllFoods(): Flow<List<FoodEntity>>
    fun searchFoods(query: String): Flow<List<FoodEntity>>
    suspend fun addCustomFood(food: FoodEntity)
    suspend fun seedMasterFoodsIfEmpty(foods: List<FoodEntity>)
}