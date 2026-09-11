package com.example.macroforge.feature_meals.presentation.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.macroforge.feature_meals.presentation.model.FoodSearchResultUiItem
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.yourname.macroforge.feature_meals.presentation.CreateMealScreen

// ---------- Previews ----------
//@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Searching")
//@Composable
//private fun CreateMealScreenSearchingPreview() {
//    MaterialTheme {
//        CreateMealScreen(
//            searchQuery = "u",
//            searchResults = listOf(
//                FoodSearchResultUiItem("s1", "Greek Yogurt", "per 150g", 146, 13.5f),
//                FoodSearchResultUiItem("s2", "Almonds", "per 28g", 164, 6f),
//                FoodSearchResultUiItem("s3", "Whey Protein", "per 31scoop", 120, 24f)
//            ),
//            foods = listOf(
//                FoodUiItem("f1", "Chicken Breast", "Raw, boneless · per 100g", 150f, "g", 248f, 46.5f, 0f, 5.4f),
//                FoodUiItem("f2", "Brown Rice", "Cooked · per 100g", 180f, "g", 202f, 4.7f, 42.3f, 1.6f),
//                FoodUiItem("f3", "Whole Egg", "Large, raw · per 50g", 100f, "g", 144f, 12.6f, 0.8f, 10f)
//            ),
//            totalCalories = 594f,
//            totalProtein = 63.8f,
//            totalCarbs = 43.1f,
//            totalFats = 17f,
//            onBack = {}, onSave = {}, onSaveMeal = {},
//            onSearchQueryChanged = {}, onClearSearch = {}, onAddFood = {},
//            onQuantityChanged = { _, _ -> }, onRemoveFood = {}
//        )
//    }
//}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Empty search")
@Composable
private fun CreateMealScreenEmptySearchPreview() {
    MaterialTheme {
        CreateMealScreen(
            searchQuery = "",
            searchResults = emptyList(),
            foods = listOf(
                FoodUiItem("f1", "Chicken Breast", "Raw, boneless · per 100g", 150f, "g", 248f, 46.5f, 0f, 5.4f)
            ),
            totalCalories = 248f,
            totalProtein = 46.5f,
            totalCarbs = 0f,
            totalFats = 5.4f,
            onBack = {}, onSave = {}, onSaveMeal = {},
            onSearchQueryChanged = {}, onClearSearch = {}, onAddFood = {},
            onQuantityChanged = { _, _ -> }, onRemoveFood = {}
        )
    }
}