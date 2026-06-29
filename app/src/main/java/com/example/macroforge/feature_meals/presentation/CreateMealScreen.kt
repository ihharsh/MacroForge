package com.example.macroforge.feature_meals.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.feature_meals.domain.usecase.MacroTotals

@Composable
fun CreateMealScreen(
    searchQuery: String,
    searchResults: List<FoodEntity>,
    selectedFoods: Map<FoodEntity, Float>,
    macros: MacroTotals,
    onSearchQueryChanged: (String) -> Unit,
    onAddFood: (FoodEntity) -> Unit,
    onUpdateQuantity: (FoodEntity, Float) -> Unit,
    onRemoveFood: (FoodEntity) -> Unit,
    onSave: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search Food")
            }
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            if (searchResults.isNotEmpty()) {

                item {
                    Text("Search Results")
                    Spacer(Modifier.height(8.dp))
                }

                items(searchResults) { food ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAddFood(food)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(food.foodName)
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (selectedFoods.isNotEmpty()) {

                item {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Food", Modifier.weight(2f))
                        Text("Qty", Modifier.weight(1f))
                        Text("Base", Modifier.weight(1f))
                        Text("Cal", Modifier.weight(0.8f))
                        Text("P", Modifier.weight(0.7f))
                        Text("C", Modifier.weight(0.7f))
                        Text("F", Modifier.weight(0.7f))
                    }

                    Divider()
                }

                items(selectedFoods.entries.toList()) { (food, qty) ->

                    val ratio = qty / food.baseNumber

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Text(
                            food.foodName,
                            modifier = Modifier.weight(2f)
                        )

                        OutlinedTextField(
                            value = qty.toString(),
                            onValueChange = {
                                it.toFloatOrNull()?.let { quantity ->
                                    onUpdateQuantity(food, quantity)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Text(
                            "${food.baseNumber} ${food.unitType}",
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            "%.0f".format(food.calories * ratio),
                            modifier = Modifier.weight(0.8f)
                        )

                        Text(
                            "%.1f".format(food.protein * ratio),
                            modifier = Modifier.weight(0.7f)
                        )

                        Text(
                            "%.1f".format(food.carbs * ratio),
                            modifier = Modifier.weight(0.7f)
                        )

                        Text(
                            "%.1f".format(food.fats * ratio),
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                item {

                    Divider()

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "TOTAL",
                            modifier = Modifier.weight(4f)
                        )

                        Text(
                            "%.0f".format(macros.calories),
                            modifier = Modifier.weight(0.8f)
                        )

                        Text(
                            "%.1f".format(macros.protein),
                            modifier = Modifier.weight(0.7f)
                        )

                        Text(
                            "%.1f".format(macros.carbs),
                            modifier = Modifier.weight(0.7f)
                        )

                        Text(
                            "%.1f".format(macros.fats),
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Meal")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateMealScreenPreview() {

    val chicken = FoodEntity(
        foodId = "1",
        foodName = "Chicken Breast",
        baseNumber = 100f,
        unitType = "g",
        calories = 165f,
        carbs = 0f,
        protein = 31f,
        fats = 3.6f
    )

    val rice = FoodEntity(
        foodId = "2",
        foodName = "Rice",
        baseNumber = 100f,
        unitType = "g",
        calories = 130f,
        carbs = 28f,
        protein = 2.7f,
        fats = 0.3f
    )

    val egg = FoodEntity(
        foodId = "3",
        foodName = "Egg",
        baseNumber = 1f,
        unitType = "pc",
        calories = 70f,
        carbs = 0.5f,
        protein = 6f,
        fats = 5f
    )

    CreateMealScreen(
        searchQuery = "",
        searchResults = listOf(chicken, rice, egg),
        selectedFoods = mapOf(
            chicken to 150f,
            rice to 200f,
            egg to 2f
        ),
        macros = MacroTotals(
            calories = 648f,
            protein = 63f,
            carbs = 57f,
            fats = 16f
        ),
        onSearchQueryChanged = {},
        onAddFood = {},
        onUpdateQuantity = { _, _ -> },
        onRemoveFood = {},
        onSave = {}
    )
}