package com.example.macroforge.feature_appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import androidx.appfunctions.AppFunctionStringValueConstraint
import com.example.macroforge.core.data.local.dao.AppFunctionMealRequestDao
import com.example.macroforge.core.data.local.entity.AppFunctionMealRequestEntity
import com.example.macroforge.feature_appfunctions.model.FoodSearchResult
import com.example.macroforge.feature_appfunctions.model.MealIngredientInput
import com.example.macroforge.feature_appfunctions.model.MealSummary
import com.example.macroforge.feature_appfunctions.model.ResolvedIngredient
import com.example.macroforge.feature_foods.domain.FoodRepository
import com.example.macroforge.feature_foods.domain.model.Food
import com.example.macroforge.feature_meals.domain.MealRepository
import com.example.macroforge.feature_meals.domain.model.Meal
import com.example.macroforge.feature_meals.domain.model.MealFoodEntry
import com.example.macroforge.feature_meals.domain.model.MealTag
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * AppFunctions entry point for MacroForge's meal-creation workflow. This is
 * a thin integration layer only — every function below delegates to the
 * same repositories and use case the Create Meal screen itself uses, so
 * meals created this way are indistinguishable from ones created by hand
 * and immediately show up in My Meals.
 *
 * Intended call sequence:
 * 1. Call `searchFoods` for each ingredient the user mentioned, to resolve
 *    it to a stable foodId and discover its serving unit.
 * 2. If a search returns more than one plausible match (e.g. a vague term
 *    like "egg" matches several catalog entries), ask the user which one
 *    they meant rather than guessing.
 * 3. Call `createMeal` with the resolved foodIds and quantities.
 * 4. Optionally call `getMeal` with the returned mealId to confirm what was
 *    actually saved.
 */
@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "MacroForgeAppFunctionService",
    appFunctionXmlFileName = "macroforge_app_function_service",
)
abstract class BaseMacroForgeAppFunctionService : AppFunctionService() {

    @Inject internal lateinit var foodRepository: FoodRepository

    @Inject internal lateinit var mealRepository: MealRepository

    @Inject internal lateinit var calculateMealMacros: CalculateMealMacrosUseCase

    @Inject internal lateinit var appFunctionMealRequestDao: AppFunctionMealRequestDao

    /**
     * Search MacroForge's food catalog by name and return stable ingredient
     * candidates. Always call this before `createMeal` — never invent a
     * foodId.
     *
     * If [query] is ambiguous (e.g. "egg" matching several egg preparations,
     * or a genuinely generic term like "veggies" matching more than one
     * vegetable entry), more than one result may be returned; in that case
     * ask the user to pick one rather than assuming the first result.
     *
     * @param query Food name or partial name to search for, e.g. "boiled eggs" or "veggies".
     * @return Matching catalog foods, closest name matches first. Empty if nothing matched —
     *   ask the user to rephrase rather than treating this as an error.
     * @throws AppFunctionInvalidArgumentException If [query] is blank.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchFoods(query: String): List<FoodSearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            throw AppFunctionInvalidArgumentException("query cannot be blank.")
        }
        foodRepository.searchFoods(query).first().map { it.toSearchResult() }
    }

    /**
     * Create and durably save a new reusable meal from resolved ingredients.
     * This is the same save operation the Create Meal screen performs, so
     * the result appears immediately in My Meals.
     *
     * All ingredients are validated before anything is saved: if any
     * [MealIngredientInput.foodId] does not resolve, or any quantity/unit is
     * invalid, no meal is created at all — never a partial one.
     *
     * Safe to retry: pass the same [idempotencyKey] again (e.g. if a
     * previous call's response was lost) and the already-created meal is
     * returned instead of creating a duplicate. Omit it if you don't need
     * that guarantee.
     *
     * @param mealName Display name for the meal, e.g. "Egg Veg Salad". Cannot be blank.
     * @param ingredients One or more resolved ingredients, each from a prior `searchFoods` call.
     *   Must not be empty.
     * @param mealTag One of BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT.
     *   Defaults to BREAKFAST if the user didn't specify a time of day.
     * @param idempotencyKey Optional stable key identifying this specific creation request, so
     *   retries don't create duplicate meals.
     * @return The saved meal, including computed nutrition totals.
     * @throws AppFunctionInvalidArgumentException If mealName is blank, ingredients is empty, a
     *   quantity is not positive, an ingredient's unit doesn't match its resolved food, or mealTag
     *   is not one of the supported values.
     * @throws AppFunctionElementNotFoundException If an ingredient's foodId does not exist. Call
     *   `searchFoods` again to find the correct id.
     * @throws AppFunctionAppUnknownException If saving fails for an unexpected reason. Suggest the
     *   user retry (with the same idempotencyKey, if one was used).
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createMeal(
        mealName: String,
        ingredients: List<MealIngredientInput>,
        @AppFunctionStringValueConstraint(
            enumValues = ["BREAKFAST", "LUNCH", "DINNER", "SNACK", "PRE_WORKOUT", "POST_WORKOUT"]
        )
        mealTag: String? = null,
        idempotencyKey: String? = null,
    ): MealSummary = withContext(Dispatchers.IO) {
        if (mealName.isBlank()) {
            throw AppFunctionInvalidArgumentException("mealName cannot be blank.")
        }
        if (ingredients.isEmpty()) {
            throw AppFunctionInvalidArgumentException(
                "At least one ingredient is required to create a meal. Call searchFoods first."
            )
        }
        val tag = parseMealTag(mealTag ?: "BREAKFAST")

        // Idempotency check: an identical retried request returns the meal
        // already created for this key instead of creating a duplicate.
        if (idempotencyKey != null) {
            val existingMealId = appFunctionMealRequestDao.findMealIdForRequest(idempotencyKey)
            if (existingMealId != null) {
                return@withContext mealRepository.getMeal(existingMealId).toSummary()
            }
        }

        // Resolve + validate every ingredient before saving anything, so an
        // unresolved or invalid ingredient never results in a partial save.
        val entries = ingredients.map { it.toValidatedEntry() }

        val now = System.currentTimeMillis()
        val meal = Meal(
            mealId = UUID.randomUUID().toString(),
            mealName = mealName.trim(),
            mealRecipe = null,
            mealTag = tag,
            ownerId = LOCAL_OWNER_ID,
            createdAt = now,
            updatedAt = now,
            syncStatus = "PENDING",
            entries = entries,
        )

        try {
            mealRepository.saveMeal(meal)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            throw AppFunctionAppUnknownException("Failed to save meal: ${error.message}")
        }

        if (idempotencyKey != null) {
            appFunctionMealRequestDao.recordRequest(
                AppFunctionMealRequestEntity(requestKey = idempotencyKey, mealId = meal.mealId)
            )
        }

        meal.toSummary()
    }

    /**
     * Retrieve a previously saved meal by id, e.g. to confirm what
     * `createMeal` actually saved.
     *
     * @param mealId A mealId previously returned by `createMeal`.
     * @return The meal's saved ingredients and nutrition totals.
     * @throws AppFunctionElementNotFoundException If no meal exists for [mealId].
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getMeal(mealId: String): MealSummary = withContext(Dispatchers.IO) {
        val meal = try {
            mealRepository.getMeal(mealId)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            throw AppFunctionElementNotFoundException("No meal found for mealId '$mealId'.")
        }
        meal.toSummary()
    }

    private suspend fun MealIngredientInput.toValidatedEntry(): MealFoodEntry {
        if (quantity <= 0f) {
            throw AppFunctionInvalidArgumentException(
                "Quantity for foodId '$foodId' must be greater than zero (was $quantity)."
            )
        }
        val food = foodRepository.getFoodById(foodId)
            ?: throw AppFunctionElementNotFoundException(
                "No food found for foodId '$foodId'. Call searchFoods first to obtain a valid foodId."
            )
        if (unit != null && !unit.equals(food.unitType, ignoreCase = true)) {
            throw AppFunctionInvalidArgumentException(
                "'${food.foodName}' is measured in '${food.unitType}', not '$unit'. Pass quantity " +
                    "in '${food.unitType}' (the unit searchFoods reported), not a converted unit."
            )
        }
        return MealFoodEntry(food, quantity)
    }

    private fun parseMealTag(rawTag: String): MealTag =
        MealTag.entries.firstOrNull { it.name.equals(rawTag, ignoreCase = true) }
            ?: throw AppFunctionInvalidArgumentException(
                "Unknown mealTag '$rawTag'. Must be one of: ${MealTag.entries.joinToString { it.name }}."
            )

    private fun Food.toSearchResult() = FoodSearchResult(
        foodId = foodId,
        foodName = foodName,
        servingAmount = baseNumber,
        servingUnit = unitType,
        caloriesPerServing = calories,
        proteinPerServing = protein,
        carbsPerServing = carbs,
        fatsPerServing = fats,
        isCustomFood = isCustom,
    )

    private fun Meal.toSummary(): MealSummary {
        val totals = calculateMealMacros(foods, quantitiesByFoodId)
        return MealSummary(
            mealId = mealId,
            mealName = mealName,
            mealTag = mealTag.name,
            ingredients = entries.map {
                ResolvedIngredient(
                    foodId = it.food.foodId,
                    foodName = it.food.foodName,
                    quantity = it.quantity,
                    unit = it.food.unitType,
                )
            },
            totalCalories = totals.calories,
            totalProtein = totals.protein,
            totalCarbs = totals.carbs,
            totalFats = totals.fats,
        )
    }

    private companion object {
        // Matches the same placeholder used elsewhere in the app (e.g.
        // CreateMealViewModel) until real authentication exists.
        const val LOCAL_OWNER_ID = "local_user"
    }
}
