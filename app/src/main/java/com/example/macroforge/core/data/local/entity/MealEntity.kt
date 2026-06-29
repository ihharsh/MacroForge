package com.example.macroforge.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// entity/MealEntity.kt
@Serializable
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val mealId: String,
    val mealName: String,
    val mealRecipe: String?,
    val mealTag: MealTag,            // needs MealTagConverter
    val ownerId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING"
)

enum class MealTag {
    BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT
}