package com.example.macroforge.core.data.local.converter

import androidx.room.TypeConverter
import com.example.macroforge.core.data.local.entity.MealTag

// converter/MealTagConverter.kt
class MealTagConverter {
    @TypeConverter
    fun fromMealTag(tag: MealTag): String = tag.name

    @TypeConverter
    fun toMealTag(value: String): MealTag = MealTag.valueOf(value)
}