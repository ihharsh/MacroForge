package com.example.macroforge.feature_meals.presentation.createMealScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.macroforge.feature_meals.domain.model.MealTag
import com.example.macroforge.feature_meals.presentation.components.*
import com.example.macroforge.feature_meals.presentation.model.FoodSearchResultUiItem
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.example.macroforge.core.ui.theme.Background
import com.example.macroforge.core.ui.theme.Orange
import com.example.macroforge.core.ui.theme.SectionHeader
import com.example.macroforge.core.ui.theme.TextGhost
import com.example.macroforge.core.ui.theme.TextSecondary


// ---------- Pure, stateless, preview-friendly screen ----------
@Composable
fun CreateMealScreen(
    searchQuery: String,
    searchResults: List<FoodSearchResultUiItem>,
    foods: List<FoodUiItem>,
    totalCalories: Float,
    totalProtein: Float,
    totalCarbs: Float,
    totalFats: Float,
    mealName: String,
    onMealNameChanged: (String) -> Unit,
    selectedTag: MealTag,
    onTagSelected: (MealTag) -> Unit,
    canSave: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onSaveMeal: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onAddFood: (FoodSearchResultUiItem) -> Unit,
    onQuantityChanged: (FoodUiItem, Float) -> Unit,
    onRemoveFood: (FoodUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            CreateMealTopBar(
                foodCount = foods.size,
                totalCalories = totalCalories,
                onBack = onBack,
                onSave = onSave,
                saveEnabled = canSave
            )
        },
        bottomBar = {
            MealTotalFooter(
                totalCalories = totalCalories,
                totalProtein = totalProtein,
                totalCarbs = totalCarbs,
                totalFats = totalFats,
                onSaveMeal = onSaveMeal,
                saveEnabled = canSave
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    query = searchQuery,
                    isActive = searchQuery.isNotEmpty(),
                    onQueryChanged = onSearchQueryChanged,
                    onClear = onClearSearch
                )

                MealNameField(
                    name = mealName,
                    onNameChanged = onMealNameChanged
                )

                MealTagSelector(
                    selectedTag = selectedTag,
                    onTagSelected = onTagSelected
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ADDED FOODS",
                        color = TextGhost,
                        style = SectionHeader
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Orange)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("${foods.size} items", color = TextSecondary, fontSize = 13.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(foods, key = { it.id }) { food ->
                        FoodEntryCard(
                            food = food,
                            onQuantityChanged = { qty -> onQuantityChanged(food, qty) },
                            onRemove = { onRemoveFood(food) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }

            }

            // Search results dropdown overlays everything below the search bar
            if (searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 76.dp)
                        .zIndex(10f)
                ) {
                    SearchResultsDropdown(
                        results = searchResults,
                        onAddFood = onAddFood
                    )
                }
            }
        }
    }
}















