package com.example.macroforge.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.macroforge.core.data.local.entity.AppFunctionMealRequestEntity

// dao/AppFunctionMealRequestDao.kt
@Dao
interface AppFunctionMealRequestDao {
    @Query("SELECT mealId FROM app_function_meal_requests WHERE requestKey = :requestKey")
    suspend fun findMealIdForRequest(requestKey: String): String?

    // IGNORE, not REPLACE: if two concurrent calls race on the same requestKey,
    // the first insert wins and the second is silently dropped rather than
    // overwriting it with a different mealId.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun recordRequest(entity: AppFunctionMealRequestEntity)
}
