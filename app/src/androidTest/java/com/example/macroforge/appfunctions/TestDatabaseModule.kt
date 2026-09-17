package com.example.macroforge.appfunctions

import android.content.Context
import androidx.room.Room
import com.example.macroforge.core.data.local.AppDatabase
import com.example.macroforge.core.data.local.dao.AppFunctionMealRequestDao
import com.example.macroforge.core.data.local.dao.FoodDao
import com.example.macroforge.core.data.local.dao.MealDao
import com.example.macroforge.core.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Swaps the real, persistent app database for an in-memory one during
 * instrumented tests, so AppFunctions instrumentation tests never touch the
 * device's actual saved meals/foods.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    @Provides
    fun provideFoodDao(db: AppDatabase): FoodDao = db.foodDao()

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideAppFunctionMealRequestDao(db: AppDatabase): AppFunctionMealRequestDao =
        db.appFunctionMealRequestDao()
}
