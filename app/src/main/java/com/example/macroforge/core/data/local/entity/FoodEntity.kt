package com.example.macroforge.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// entity/FoodEntity.kt
@Serializable
@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey val foodId: String,
    val foodName: String,
    val baseNumber: Float,
    val unitType: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fats: Float,
    val isCustom: Boolean = false,
    val ownerId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)