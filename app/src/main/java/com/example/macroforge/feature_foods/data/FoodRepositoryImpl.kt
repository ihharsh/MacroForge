package com.example.macroforge.feature_foods.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.macroforge.core.data.local.dao.FoodDao
import com.example.macroforge.feature_foods.data.mapper.toDomain
import com.example.macroforge.feature_foods.data.mapper.toEntity
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// feature_foods/data/FoodRepositoryImpl.kt
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {

    override fun getAllFoods(): Flow<List<Food>> =
        foodDao.getAllFoods().map { entities -> entities.map { it.toDomain() } }

    override fun searchFoods(query: String): Flow<List<Food>> =
        foodDao.searchFoods(query).map { entities -> entities.map { it.toDomain() } }

    override fun getPagedFoods(query: String): Flow<PagingData<Food>> =
        Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { foodDao.getFoodsPaged(query) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override suspend fun getFoodById(foodId: String): Food? =
        foodDao.getFoodById(foodId)?.toDomain()

    override suspend fun addCustomFood(food: Food) {
        foodDao.insert(food.toEntity())
    }

    override suspend fun seedMasterFoodsIfEmpty(foods: List<Food>) {
        if (foodDao.getCount() == 0) {
            foodDao.insertAll(foods.map { it.toEntity() })
        }
    }
}
