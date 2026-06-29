package com.example.macroforge.feature_foods.data

import com.example.macroforge.core.data.local.dao.FoodDao
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.feature_foods.domain.FoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// feature_foods/data/FoodRepositoryImpl.kt
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {

    override fun getAllFoods(): Flow<List<FoodEntity>> = foodDao.getAllFoods()

    override fun searchFoods(query: String): Flow<List<FoodEntity>> =
        foodDao.searchFoods(query)

    override suspend fun addCustomFood(food: FoodEntity) {
        foodDao.insert(food)
    }

    override suspend fun seedMasterFoodsIfEmpty(foods: List<FoodEntity>) {
        if (foodDao.getCount() == 0) {
            foodDao.insertAll(foods)
        }
    }
}