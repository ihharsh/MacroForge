package com.example.macroforge.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// entity/AppFunctionMealRequestEntity.kt
//
// Backs createMeal's idempotency mechanism: when a caller (e.g. the
// assistant, retrying after a dropped response) supplies the same
// requestKey twice, the second call returns the meal already created for
// that key instead of creating a duplicate.
@Entity(tableName = "app_function_meal_requests")
data class AppFunctionMealRequestEntity(
    @PrimaryKey val requestKey: String,
    val mealId: String,
    val createdAt: Long = System.currentTimeMillis()
)
