package com.example.macroforge.core.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef

// relation/MealWithFoods.kt
data class MealWithFoods(

    @Embedded
    val meal: MealEntity,

    @Relation(
        parentColumn = "mealId",
        entityColumn = "foodId",
        associateBy = Junction(MealFoodCrossRef::class)
    )
    val foods: List<FoodEntity>,

    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId"
    )
    val crossRefs: List<MealFoodCrossRef>
)