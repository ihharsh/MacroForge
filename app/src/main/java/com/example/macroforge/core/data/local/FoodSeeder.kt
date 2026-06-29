package com.example.macroforge.core.data.local

import android.content.Context
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.feature_foods.domain.FoodRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject


// core/data/local/FoodSeeder.kt
class FoodSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodRepository: FoodRepository
) {
    suspend fun seedIfNeeded() {
        val json = context.assets.open("seed_foods.json")
            .bufferedReader().use { it.readText() }
        val foods: List<FoodEntity> = Json.decodeFromString(json)
        foodRepository.seedMasterFoodsIfEmpty(foods)
    }
}