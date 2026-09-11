package com.example.macroforge.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.core.data.local.relation.MealWithFoods
import kotlinx.coroutines.flow.Flow

// dao/MealDao.kt
@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Insert
    suspend fun insertCrossRefs(crossRefs: List<MealFoodCrossRef>)

    @Transaction
    @Query("SELECT * FROM meals WHERE mealId = :mealId")
    suspend fun getMealWithFoods(mealId: String): MealWithFoods

    @Transaction
    @Query("SELECT * FROM meals ORDER BY createdAt DESC")
    fun getAllMealsWithFoods(): Flow<List<MealWithFoods>>

    // Empty string for :tag/:query means "no filter on this field" — an empty
    // tag matches every mealTag vacuously, an empty query matches every name.
    @Transaction
    @Query(
        "SELECT * FROM meals " +
        "WHERE (:tag = '' OR mealTag = :tag) " +
        "AND (:query = '' OR mealName LIKE '%' || :query || '%') " +
        "ORDER BY createdAt DESC"
    )
    fun searchMeals(query: String, tag: String): Flow<List<MealWithFoods>>

    @Query("SELECT * FROM meal_food_cross_ref WHERE mealId = :mealId")
    suspend fun getCrossRefsForMeal(mealId: String): List<MealFoodCrossRef>

    @Query("DELETE FROM meals WHERE mealId = :mealId")
    suspend fun deleteMeal(mealId: String)
}