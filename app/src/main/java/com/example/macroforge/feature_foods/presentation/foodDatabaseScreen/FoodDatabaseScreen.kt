package com.example.macroforge.feature_foods.presentation.foodDatabaseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.core.ui.components.SearchBar
import com.example.macroforge.core.ui.theme.Background
import com.example.macroforge.core.ui.theme.Orange
import com.example.macroforge.core.ui.theme.OrangeBg
import com.example.macroforge.core.ui.theme.Surface
import com.example.macroforge.core.ui.theme.TextGhost
import com.example.macroforge.core.ui.theme.TextPrimary
import com.example.macroforge.core.ui.theme.TextSecondary
import com.example.macroforge.core.util.formatMacro
import com.example.macroforge.feature_foods.presentation.model.FoodBrowseUiItem

@Composable
fun FoodDatabaseScreen(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    foodsState: UiState<List<FoodBrowseUiItem>>,
    onAddFoodClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFoodClicked,
                containerColor = Orange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Food Database",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            SearchBar(
                query = searchQuery,
                isActive = searchQuery.isNotEmpty(),
                onQueryChanged = onSearchQueryChanged,
                onClear = { onSearchQueryChanged("") },
                placeholder = "Search food database..."
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (foodsState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Orange
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = foodsState.message,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp),
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    is UiState.Success -> {
                        val foods = foodsState.data
                        if (foods.isEmpty()) {
                            Text(
                                text = "No foods found",
                                modifier = Modifier.align(Alignment.Center),
                                color = TextSecondary
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(foods, key = { it.id }) { food ->
                                    FoodBrowseRow(food)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodBrowseRow(food: FoodBrowseUiItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(food.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                    if (food.isCustom) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("CUSTOM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Orange)
                        }
                    }
                }
                Text(food.subtitle, color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${food.calories} kcal", color = Orange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("${formatMacro(food.protein)}g protein", color = TextGhost, fontSize = 11.sp)
            }
        }
    }
}
