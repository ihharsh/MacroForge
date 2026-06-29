package com.example.macroforge.feature_meals.data

import androidx.room.withTransaction
import com.example.macroforge.core.data.local.AppDatabase
import com.example.macroforge.core.data.local.dao.MealDao
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.core.data.local.relation.MealWithFoods
import com.example.macroforge.feature_meals.domain.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// feature_meals/data/MealRepositoryImpl.kt
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val database: AppDatabase
) : MealRepository {

    override fun getAllMeals(): Flow<List<MealWithFoods>> = mealDao.getAllMealsWithFoods()

    override suspend fun getMeal(mealId: String): MealWithFoods = mealDao.getMealWithFoods(mealId)

    override suspend fun saveMeal(meal: MealEntity, foodEntries: List<MealFoodCrossRef>) {
        database.withTransaction {
            mealDao.insertMeal(meal)
            mealDao.insertCrossRefs(foodEntries)
        }
    }

    override fun searchMeals(query: String, tag: String): Flow<List<MealEntity>> =
        mealDao.searchMeals(query, tag)
}