package com.example.macroforge.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.macroforge.core.data.local.converter.MealTagConverter
import com.example.macroforge.core.data.local.dao.FoodDao
import com.example.macroforge.core.data.local.dao.MealDao
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef

// AppDatabase.kt
@Database(
    entities = [FoodEntity::class, MealEntity::class, MealFoodCrossRef::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(MealTagConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
}