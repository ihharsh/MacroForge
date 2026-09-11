package com.example.macroforge.feature_meals.presentation.util


import com.example.macroforge.core.data.local.entity.MealTag
import com.example.macroforge.feature_meals.presentation.savedMealScreen.MealTagUi
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// Extensions — bridging domain ↔ UI layer
// Place in: feature_meals/presentation/util/MealMappingExtensions.kt
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Maps domain MealTag enum → UI MealTagUi enum for display.
 * Called in SavedMealsRoute when building SavedMealUiItem from MealEntity.
 */
fun MealTag.toUiTag(): MealTagUi = when (this) {
    MealTag.BREAKFAST    -> MealTagUi.BREAKFAST
    MealTag.LUNCH        -> MealTagUi.LUNCH
    MealTag.DINNER       -> MealTagUi.DINNER
    MealTag.SNACK        -> MealTagUi.SNACK
    MealTag.PRE_WORKOUT  -> MealTagUi.PRE_WORKOUT
    MealTag.POST_WORKOUT -> MealTagUi.POST_WORKOUT
}

/**
 * Maps UI MealTagUi → domain MealTag for repository search queries.
 * Used when passing the selected filter tag to MealRepository.searchMeals().
 * Returns null for MealTagUi.ALL (meaning no tag filter — fetch everything).
 */
fun MealTagUi.toDomainTag(): MealTag? = when (this) {
    MealTagUi.ALL          -> null
    MealTagUi.BREAKFAST    -> MealTag.BREAKFAST
    MealTagUi.LUNCH        -> MealTag.LUNCH
    MealTagUi.DINNER       -> MealTag.DINNER
    MealTagUi.SNACK        -> MealTag.SNACK
    MealTagUi.PRE_WORKOUT  -> MealTag.PRE_WORKOUT
    MealTagUi.POST_WORKOUT -> MealTag.POST_WORKOUT
}

/**
 * Formats a Unix timestamp (Long) to a readable time string like "7:30 AM".
 * Called in SavedMealsRoute when mapping MealEntity.createdAt to UiItem.time.
 */
fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}