package com.example.macroforge.feature_meals.presentation.savedMealScreen

import com.example.macroforge.feature_meals.presentation.util.toFormattedTime
import com.example.macroforge.feature_meals.presentation.util.toUiTag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var pendingDeleteMeal by remember { mutableStateOf<SavedMealUiItem?>(null) }

    // Map domain Meal -> UI model
    val mealUiItems = remember(meals) {
        meals.map { meal ->
            var totalCal = 0f
            var totalProtein = 0f
            var totalCarbs = 0f
            var totalFats = 0f

            meal.entries.forEach { entry ->
                val ratio = entry.quantity / entry.food.baseNumber
                totalCal     += entry.food.calories * ratio
                totalProtein += entry.food.protein  * ratio
                totalCarbs   += entry.food.carbs    * ratio
                totalFats    += entry.food.fats     * ratio
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
        onProfileClicked   = onNavigateToProfile,
        onDeleteMeal       = { uiItem -> pendingDeleteMeal = uiItem }
    )

    pendingDeleteMeal?.let { meal ->
        AlertDialog(
            onDismissRequest = { pendingDeleteMeal = null },
            title = { Text("Delete \"${meal.name}\"?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteMeal(meal.id)
                    pendingDeleteMeal = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteMeal = null }) { Text("Cancel") }
            }
        )
    }
}
