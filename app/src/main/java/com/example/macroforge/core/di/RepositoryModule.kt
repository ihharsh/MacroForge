package com.example.macroforge.core.di

import com.example.macroforge.feature_foods.data.FoodRepositoryImpl
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_meals.data.MealRepositoryImpl
import com.example.macroforge.feature_meals.domain.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// core/di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository
}