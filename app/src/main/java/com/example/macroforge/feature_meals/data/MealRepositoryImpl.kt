package com.example.macroforge.feature_meals.data

import androidx.room.withTransaction
import com.example.macroforge.core.data.local.AppDatabase
import com.example.macroforge.core.data.local.dao.MealDao
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.feature_meals.data.mapper.toDomain
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// feature_meals/data/MealRepositoryImpl.kt
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val database: AppDatabase
) : MealRepository {

    override fun getAllMeals(): Flow<List<Meal>> =
        mealDao.getAllMealsWithFoods().map { meals -> meals.map { it.toDomain() } }

    override suspend fun getMeal(mealId: String): Meal =
        mealDao.getMealWithFoods(mealId).toDomain()

    override suspend fun saveMeal(meal: Meal) {
        val mealEntity = MealEntity(
            mealId = meal.mealId,
            mealName = meal.mealName,
            mealRecipe = meal.mealRecipe,
            mealTag = meal.mealTag,
            ownerId = meal.ownerId,
            createdAt = meal.createdAt,
            updatedAt = meal.updatedAt,
            syncStatus = meal.syncStatus
        )
        val crossRefs = meal.entries.map { entry ->
            MealFoodCrossRef(meal.mealId, entry.food.foodId, entry.quantity)
        }
        database.withTransaction {
            mealDao.insertMeal(mealEntity)
            mealDao.insertCrossRefs(crossRefs)
        }
    }

    override suspend fun deleteMeal(mealId: String) {
        mealDao.deleteMeal(mealId)
    }

    override fun searchMeals(query: String, tag: MealTag?): Flow<List<Meal>> =
        mealDao.searchMeals(query, tag?.name ?: "").map { meals -> meals.map { it.toDomain() } }
}
