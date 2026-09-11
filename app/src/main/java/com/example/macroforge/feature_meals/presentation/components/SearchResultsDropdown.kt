package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.feature_meals.presentation.model.FoodSearchResultUiItem
import com.example.macroforge.feature_meals.presentation.util.formatMacro
import com.example.macroforge.core.ui.theme.Background
import com.example.macroforge.core.ui.theme.Blue
import com.example.macroforge.core.ui.theme.Orange
import com.example.macroforge.core.ui.theme.TextGhost

@Composable
fun SearchResultsDropdown(
    results: List<FoodSearchResultUiItem>,
    onAddFood: (FoodSearchResultUiItem) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Background,
        shadowElevation = 12.dp,
        border = BorderStroke(1.5.dp, Orange.copy(alpha = 0.6f))
    ) {
        Column {
            results.forEachIndexed { index, result ->
                SearchResultRow(result = result, onAdd = { onAddFood(result) })
                if (index != results.lastIndex) {
                    HorizontalDivider(color = Background, thickness = 1.dp)
                }
            }
            if (results.isEmpty()) {
                Text(
                    "No matching foods",
                    color = TextGhost,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    result: FoodSearchResultUiItem,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(result.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Orange)
            Spacer(Modifier.height(2.dp))
            Row {
                Text(result.subtitle, fontSize = 13.sp, color = TextGhost)
                Text(" · ", fontSize = 13.sp, color = TextGhost)
                Text("${result.calories} kcal", fontSize = 13.sp, color = Orange, fontWeight = FontWeight.Medium)
                Text(" · ", fontSize = 13.sp, color = TextGhost)
                Text("${formatMacro(result.protein)}g P", fontSize = 13.sp, color = Blue, fontWeight = FontWeight.Medium)
            }
        }
        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Orange)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add ${result.name}", tint = Color.White)
        }
    }
}