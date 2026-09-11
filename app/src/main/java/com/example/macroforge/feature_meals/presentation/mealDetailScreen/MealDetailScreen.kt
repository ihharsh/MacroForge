package com.example.macroforge.feature_meals.presentation.mealDetailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.ui.theme.Amber
import com.example.macroforge.core.ui.theme.AmberBg
import com.example.macroforge.core.ui.theme.Background
import com.example.macroforge.core.ui.theme.Blue
import com.example.macroforge.core.ui.theme.BlueBg
import com.example.macroforge.core.ui.theme.Green
import com.example.macroforge.core.ui.theme.GreenBg
import com.example.macroforge.core.ui.theme.Orange
import com.example.macroforge.core.ui.theme.OrangeBg
import com.example.macroforge.core.ui.theme.Surface
import com.example.macroforge.core.ui.theme.TextGhost
import com.example.macroforge.core.ui.theme.TextPrimary
import com.example.macroforge.core.ui.theme.TextSecondary
import com.example.macroforge.feature_meals.presentation.components.MacroPill
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.example.macroforge.feature_meals.presentation.util.formatMacro

@Composable
fun MealDetailScreen(
    uiState: MealDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(color = Surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = (uiState as? MealDetailUiState.Loaded)?.meal?.mealName ?: "Meal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is MealDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Orange
                    )
                }
                is MealDetailUiState.NotFound -> {
                    Text(
                        text = "Meal not found",
                        modifier = Modifier.align(Alignment.Center),
                        color = TextSecondary
                    )
                }
                is MealDetailUiState.Loaded -> {
                    MealDetailContent(uiState.meal)
                }
            }
        }
    }
}

@Composable
private fun MealDetailContent(meal: MealDetailUiModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(meal.tagLabel, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(meal.time, color = TextSecondary, fontSize = 13.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MacroPill(
                value = meal.totalCalories.toInt().toString(),
                label = "KCAL",
                valueColor = Orange,
                bgColor = OrangeBg,
                showGramSuffix = false,
                modifier = Modifier.weight(1f)
            )
            MacroPill(
                value = formatMacro(meal.totalProtein),
                label = "PROTEIN",
                valueColor = Blue,
                bgColor = BlueBg,
                modifier = Modifier.weight(1f)
            )
            MacroPill(
                value = formatMacro(meal.totalCarbs),
                label = "CARBS",
                valueColor = Amber,
                bgColor = AmberBg,
                modifier = Modifier.weight(1f)
            )
            MacroPill(
                value = formatMacro(meal.totalFats),
                label = "FAT",
                valueColor = Green,
                bgColor = GreenBg,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "FOODS",
            color = TextGhost,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(meal.foods, key = { it.id }) { food ->
                MealDetailFoodRow(food)
            }
        }
    }
}

@Composable
private fun MealDetailFoodRow(food: FoodUiItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(food.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                Text("${formatMacro(food.quantity)}${food.unit}", color = TextSecondary, fontSize = 12.sp)
            }
            Text("${food.calories.toInt()} kcal", color = Orange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}
