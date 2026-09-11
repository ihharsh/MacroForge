package com.example.macroforge.feature_meals.presentation.savedMealScreen

import com.example.macroforge.feature_meals.presentation.util.toFormattedTime
import com.example.macroforge.feature_meals.presentation.util.toUiTag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SavedMealsRoute(
    onNavigateToCreateMeal: () -> Unit,
    onNavigateToMealDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToFoods: () -> Unit,
    onNavigateToLog: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SavedMealsViewModel = hiltViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val totalKcalToday by viewModel.totalKcalToday.collectAsStateWithLifecycle()

    // Map domain MealWithFoods -> UI model
    // Will be populated once MealRepository is wired in ViewModel
    val mealUiItems = remember(meals) {
        meals.map { mealWithFoods ->
            val meal = mealWithFoods.meal

            // Scale each food's macros by quantity via cross-ref
            // and sum for meal totals
            val crossRefs = mealWithFoods.crossRefs   // List<MealFoodCrossRef>
            val foods = mealWithFoods.foods            // List<FoodEntity>

            var totalCal = 0f
            var totalProtein = 0f
            var totalCarbs = 0f
            var totalFats = 0f

            foods.forEach { food ->
                val qty = crossRefs
                    .firstOrNull { it.foodId == food.foodId }
                    ?.quantity ?: 0f
                val ratio = qty / food.baseNumber
                totalCal     += food.calories * ratio
                totalProtein += food.protein  * ratio
                totalCarbs   += food.carbs    * ratio
                totalFats    += food.fats     * ratio
            }

            SavedMealUiItem(
                id          = meal.mealId,
                name        = meal.mealName,
                tag         = meal.mealTag.toUiTag(),
                time        = meal.createdAt.toFormattedTime(),
                calories    = totalCal.toInt(),
                protein     = totalProtein,
                carbs       = totalCarbs,
                fats        = totalFats,
                syncStatus  = if (meal.syncStatus == "SYNCED") SyncStatus.SYNCED
                else SyncStatus.PENDING,
                calorieGoal = 800   // TODO: pull from user profile/settings
            )
        }
    }

    SavedMealsScreen(
        meals              = mealUiItems,
        selectedTag        = selectedTag,
        searchQuery        = searchQuery,
        isSearchActive     = isSearchActive,
        totalKcalToday     = totalKcalToday,
        onTagSelected      = viewModel::onTagSelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchToggled    = viewModel::onSearchToggled,
        onCancelSearch     = viewModel::onCancelSearch,
        onMealClicked      = { uiItem -> onNavigateToMealDetail(uiItem.id) },
        onCreateMeal       = onNavigateToCreateMeal,
        onFilterClicked    = { /* TODO: show filter bottom sheet */ },
        onHomeClicked      = onNavigateToHome,
        onFoodsClicked     = onNavigateToFoods,
        onLogClicked       = onNavigateToLog,
        onProfileClicked   = onNavigateToProfile
    )
}