package com.example.macroforge

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.macroforge.core.data.local.AppDatabase
import com.example.macroforge.core.data.local.dao.FoodDao
import com.example.macroforge.core.data.local.dao.MealDao
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.core.data.local.entity.MealEntity
import com.example.macroforge.core.data.local.entity.MealFoodCrossRef
import com.example.macroforge.feature_meals.domain.model.MealTag
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// androidTest/.../data/MealDaoTest.kt
@RunWith(AndroidJUnit4::class)
class MealDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var foodDao: FoodDao
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        // in-memory DB — wiped after test, doesn't touch real app data
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries() // ok for tests only, never in production code
            .build()

        foodDao = db.foodDao()
        mealDao = db.mealDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertMealWithFoods_andReadBack() = runTest {
        val food = FoodEntity(
            foodId = "f1", foodName = "Egg", baseNumber = 1f, unitType = "egg",
            calories = 77f, carbs = 0.6f, protein = 6.5f, fats = 5.2f
        )
        foodDao.insert(food)

        val meal = MealEntity(
            mealId = "m1", mealName = "Test Meal", mealRecipe = null,
            mealTag = MealTag.BREAKFAST, ownerId = "user1"
        )
        mealDao.insertMeal(meal)
        mealDao.insertCrossRefs(listOf(MealFoodCrossRef("m1", "f1", 150f)))

        val result = mealDao.getMealWithFoods("m1")

        TestCase.assertEquals(1, result.foods.size)
        TestCase.assertEquals("Egg", result.foods[0].foodName)
    }
}