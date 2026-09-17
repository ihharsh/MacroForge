package com.example.macroforge.appfunctions

import android.content.Context
import androidx.appfunctions.AppFunctionData
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionManager
import androidx.appfunctions.AppFunctionSearchSpec
import androidx.appfunctions.ExecuteAppFunctionRequest
import androidx.appfunctions.ExecuteAppFunctionResponse
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.macroforge.feature_appfunctions.MacroForgeAppFunctionService
import com.example.macroforge.feature_appfunctions.model.MealIngredientInput
import com.example.macroforge.feature_appfunctions.model.MealSummary
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import com.example.macroforge.feature_meals.domain.MealRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import javax.inject.Inject

/**
 * Instrumented test for MacroForge's AppFunctions integration, executed
 * through the real [AppFunctionManager] path (the same mechanism Assistant
 * uses), not by calling [BaseMacroForgeAppFunctionService]'s methods
 * directly. Verifies the Egg Veg Salad example end to end, plus the
 * validation/atomicity/idempotency rules the design requires.
 *
 * Foods are seeded directly (not from the production seed_foods.json asset)
 * so this test is self-contained and fast; values are copied verbatim from
 * that asset's real "Boiled Eggs" (f012) and "Veggies (mixed)" (f111)
 * entries. The database is swapped for an in-memory instance by
 * [TestDatabaseModule], so this never touches a real device's saved meals.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MacroForgeAppFunctionsInstrumentationTest {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var foodRepository: FoodRepository

    @Inject lateinit var mealRepository: MealRepository

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val appFunctionManager: AppFunctionManager =
        checkNotNull(AppFunctionManager.getInstance(context))

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()
        foodRepository.addCustomFood(
            Food(
                foodId = "f012",
                foodName = "Boiled Eggs",
                baseNumber = 1f,
                unitType = "egg",
                calories = 77f,
                carbs = 0.56f,
                protein = 6.5f,
                fats = 5.2f,
            )
        )
        foodRepository.addCustomFood(
            Food(
                foodId = "f111",
                foodName = "Veggies (mixed)",
                baseNumber = 100f,
                unitType = "g",
                calories = 54.7f,
                carbs = 12f,
                protein = 1f,
                fats = 0.3f,
            )
        )
        // Other real catalog entries containing "egg", to reproduce the
        // ambiguous-search case without depending on the full seed asset.
        listOf("Boiled Egg Whites" to "f013", "Egg Whites (raw)" to "f014", "Fried Eggs" to "f015")
            .forEach { (name, id) ->
                foodRepository.addCustomFood(
                    Food(
                        foodId = id,
                        foodName = name,
                        baseNumber = 1f,
                        unitType = "egg",
                        calories = 50f,
                        carbs = 0f,
                        protein = 5f,
                        fats = 2f,
                    )
                )
            }
    }

    private suspend fun functionMetadata(functionId: String) =
        appFunctionManager
            .observeAppFunctions(AppFunctionSearchSpec(packageNames = setOf(context.packageName)))
            .first()
            .flatMap { it.appFunctions }
            .single { it.id == functionId }

    private suspend fun executeSearchFoods(query: String): ExecuteAppFunctionResponse {
        val metadata = functionMetadata(MacroForgeAppFunctionService.FUNCTION_ID_SEARCH_FOODS)
        val request = ExecuteAppFunctionRequest(
            targetPackageName = context.packageName,
            MacroForgeAppFunctionService.FUNCTION_ID_SEARCH_FOODS,
            AppFunctionData.Builder(metadata.parameters, metadata.components)
                .setString("query", query)
                .build(),
        )
        return appFunctionManager.executeAppFunction(request)
    }

    private suspend fun executeCreateMeal(
        mealName: String,
        ingredients: List<MealIngredientInput>,
        mealTag: String? = null,
        idempotencyKey: String? = null,
    ): ExecuteAppFunctionResponse {
        val metadata = functionMetadata(MacroForgeAppFunctionService.FUNCTION_ID_CREATE_MEAL)
        val builder = AppFunctionData.Builder(metadata.parameters, metadata.components)
            .setString("mealName", mealName)
            .setAppFunctionDataList(
                "ingredients",
                ingredients.map { AppFunctionData.serialize(it, MealIngredientInput::class.java) },
            )
        if (mealTag != null) builder.setString("mealTag", mealTag)
        if (idempotencyKey != null) builder.setString("idempotencyKey", idempotencyKey)
        val request = ExecuteAppFunctionRequest(
            targetPackageName = context.packageName,
            MacroForgeAppFunctionService.FUNCTION_ID_CREATE_MEAL,
            builder.build(),
        )
        return appFunctionManager.executeAppFunction(request)
    }

    private suspend fun executeGetMeal(mealId: String): ExecuteAppFunctionResponse {
        val metadata = functionMetadata(MacroForgeAppFunctionService.FUNCTION_ID_GET_MEAL)
        val request = ExecuteAppFunctionRequest(
            targetPackageName = context.packageName,
            MacroForgeAppFunctionService.FUNCTION_ID_GET_MEAL,
            AppFunctionData.Builder(metadata.parameters, metadata.components)
                .setString("mealId", mealId)
                .build(),
        )
        return appFunctionManager.executeAppFunction(request)
    }

    private fun ExecuteAppFunctionResponse.Success.mealSummary(): MealSummary =
        checkNotNull(returnValue.getAppFunctionData(ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE))
            .deserialize(MealSummary::class.java)

    private fun asSuccess(response: ExecuteAppFunctionResponse): ExecuteAppFunctionResponse.Success {
        assertTrue("expected Success but was $response", response is ExecuteAppFunctionResponse.Success)
        return response as ExecuteAppFunctionResponse.Success
    }

    private fun asError(response: ExecuteAppFunctionResponse): ExecuteAppFunctionResponse.Error {
        assertTrue("expected Error but was $response", response is ExecuteAppFunctionResponse.Error)
        return response as ExecuteAppFunctionResponse.Error
    }

    private inline fun <reified T : Throwable> assertErrorType(error: Throwable) {
        assertTrue(
            "expected ${T::class.simpleName} but was ${error::class.simpleName}: ${error.message}",
            error is T,
        )
    }

    @Test
    fun searchFoods_boiledEggsQuery_resolvesToSingleMatch() = runBlocking {
        val response = executeSearchFoods("boiled eggs")
        val success = asSuccess(response)
        val results = success.returnValue
            .getAppFunctionDataList(ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE)
        assertEquals(1, results?.size)
    }

    @Test
    fun searchFoods_veggiesQuery_resolvesToSingleMatch() = runBlocking {
        val response = executeSearchFoods("veggies")
        val success = asSuccess(response)
        val results = success.returnValue
            .getAppFunctionDataList(ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE)
        assertEquals(1, results?.size)
    }

    @Test
    fun searchFoods_ambiguousEggQuery_returnsMultipleMatches() = runBlocking {
        val response = executeSearchFoods("egg")
        val success = asSuccess(response)
        val results = success.returnValue
            .getAppFunctionDataList(ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE)
        assertTrue("expected more than one ambiguous match", (results?.size ?: 0) > 1)
    }

    @Test
    fun searchFoods_blankQuery_fails() = runBlocking {
        val response = executeSearchFoods("   ")
        val error = asError(response)
        assertErrorType<AppFunctionInvalidArgumentException>(error.error)
    }

    @Test
    fun createMeal_eggVegSalad_savesWithCorrectMacrosAndIsRetrievable() = runBlocking {
        val response = executeCreateMeal(
            mealName = "Egg Veg Salad",
            ingredients = listOf(
                MealIngredientInput(foodId = "f111", quantity = 500f, unit = "g"),
                MealIngredientInput(foodId = "f012", quantity = 4f, unit = "egg"),
            ),
        )
        val success = asSuccess(response)
        val summary = success.mealSummary()

        assertEquals("Egg Veg Salad", summary.mealName)
        assertEquals(2, summary.ingredients.size)
        assertEquals(581.5f, summary.totalCalories, 0.01f)
        assertEquals(31f, summary.totalProtein, 0.01f)
        assertEquals(62.24f, summary.totalCarbs, 0.01f)
        assertEquals(22.3f, summary.totalFats, 0.01f)

        // Confirm it's durably saved — readable through the same repository
        // the My Meals screen observes, not just returned in the response.
        val persisted = mealRepository.getMeal(summary.mealId)
        assertEquals("Egg Veg Salad", persisted.mealName)
        assertEquals(2, persisted.entries.size)
    }

    @Test
    fun createMeal_missingFoodId_fails_andSavesNothing() = runBlocking {
        val mealsBefore = mealRepository.getAllMeals().first().size

        val response = executeCreateMeal(
            mealName = "Broken Meal",
            ingredients = listOf(
                MealIngredientInput(foodId = "f111", quantity = 500f, unit = "g"),
                MealIngredientInput(foodId = "does-not-exist", quantity = 1f, unit = null),
            ),
        )
        val error = asError(response)
        assertErrorType<AppFunctionElementNotFoundException>(error.error)

        // Atomic: the valid ingredient must not have been saved as a partial meal.
        val mealsAfter = mealRepository.getAllMeals().first().size
        assertEquals(mealsBefore, mealsAfter)
    }

    @Test
    fun createMeal_nonPositiveQuantity_fails() = runBlocking {
        val response = executeCreateMeal(
            mealName = "Zero Qty Meal",
            ingredients = listOf(MealIngredientInput(foodId = "f012", quantity = 0f, unit = "egg")),
        )
        val error = asError(response)
        assertErrorType<AppFunctionInvalidArgumentException>(error.error)
    }

    @Test
    fun createMeal_unitMismatch_fails() = runBlocking {
        // f012 (Boiled Eggs) is measured in "egg", not "g" — a caller-supplied
        // unit that doesn't match must be rejected, not silently reinterpreted.
        val response = executeCreateMeal(
            mealName = "Unit Mismatch Meal",
            ingredients = listOf(MealIngredientInput(foodId = "f012", quantity = 4f, unit = "g")),
        )
        val error = asError(response)
        assertErrorType<AppFunctionInvalidArgumentException>(error.error)
    }

    @Test
    fun createMeal_repeatedIdempotencyKey_doesNotDuplicate() = runBlocking {
        val key = UUID.randomUUID().toString()
        val ingredients = listOf(MealIngredientInput(foodId = "f012", quantity = 2f, unit = "egg"))

        val first = executeCreateMeal("Idempotent Meal", ingredients, idempotencyKey = key)
        val firstSummary = asSuccess(first).mealSummary()

        val second = executeCreateMeal("Idempotent Meal", ingredients, idempotencyKey = key)
        val secondSummary = asSuccess(second).mealSummary()

        assertEquals(firstSummary.mealId, secondSummary.mealId)
        val mealCount = mealRepository.getAllMeals().first().count { it.mealName == "Idempotent Meal" }
        assertEquals(1, mealCount)
    }

    @Test
    fun getMeal_unknownId_fails() = runBlocking {
        val response = executeGetMeal("no-such-meal-id")
        val error = asError(response)
        assertErrorType<AppFunctionElementNotFoundException>(error.error)
    }
}
