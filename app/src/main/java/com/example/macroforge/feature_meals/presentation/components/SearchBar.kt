package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.macroforge.core.ui.theme.ComponentDefaults.searchTextFieldColors
import com.yourname.macroforge.core.ui.theme.Orange
import com.yourname.macroforge.core.ui.theme.SearchPlaceholder
import com.yourname.macroforge.core.ui.theme.TextGhost
import com.yourname.macroforge.core.ui.theme.TextSecondary

@Composable
fun SearchBar(
    query: String,
    isActive: Boolean,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Search foods...", style = SearchPlaceholder, color = TextSecondary) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = if (isActive) Orange else TextGhost)
        },
        trailingIcon = {
            if (isActive) {
                IconButton(onClick = onClear, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGhost, modifier = Modifier.size(16.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = searchTextFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .let { if (isActive) it.shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)) else it }
    )
}