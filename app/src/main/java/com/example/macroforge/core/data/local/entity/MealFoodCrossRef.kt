package com.example.macroforge.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

// entity/MealFoodCrossRef.kt
@Serializable
@Entity(
    tableName = "meal_food_cross_ref",
    primaryKeys = ["mealId", "foodId"],
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["mealId"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = FoodEntity::class, parentColumns = ["foodId"], childColumns = ["foodId"])
    ],
    indices = [Index("foodId")]
)
data class MealFoodCrossRef(
    val mealId: String,
    val foodId: String,
    val quantity: Float
)