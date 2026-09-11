package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.ui.theme.BorderSolid
import com.example.macroforge.core.ui.theme.ComponentDefaults.searchTextFieldColors
import com.example.macroforge.core.ui.theme.Orange
import com.example.macroforge.core.ui.theme.TextSecondary
import com.example.macroforge.feature_meals.domain.model.MealTag
import com.example.macroforge.feature_meals.presentation.util.displayLabel

@Composable
fun MealNameField(
    name: String,
    onNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChanged,
        placeholder = { Text("Name this meal…") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = searchTextFieldColors(),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
fun MealTagSelector(
    selectedTag: MealTag,
    onTagSelected: (MealTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MealTag.entries.forEach { tag ->
            val isSelected = tag == selectedTag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Orange else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) Orange else BorderSolid,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onTagSelected(tag) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tag.displayLabel(),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondary
                )
            }
        }
    }
}
