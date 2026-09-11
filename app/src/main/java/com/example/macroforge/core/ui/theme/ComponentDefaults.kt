package com.example.macroforge.core.ui.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ComponentDefaults {

    @Composable
    fun buttonColors() =
        ButtonDefaults.buttonColors(
            containerColor = Orange
        )

    @Composable
    fun cardColors() =
        CardDefaults.cardColors(
            containerColor = Surface
        )

    @Composable
    fun searchTextFieldColors() =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            focusedBorderColor = Orange,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )

    @Composable
    fun quantityTextFieldColors() =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Background,
            unfocusedContainerColor = Background,
            focusedBorderColor = TextSecondary.copy(alpha = 0.3f),
            unfocusedBorderColor = Color.Transparent
        )
}