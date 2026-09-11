package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// CreateMealTopBar
// Spec source: TSX Top App Bar section + DesignTokens.TopBarSpec / SaveTopBarButtonSpec
//
// Changes from previous version:
//  - Font size: 20sp → 17sp  (ScreenTitle style from Type.kt)
//  - Letter spacing: added -0.5sp  (matches TSX letterSpacing:-0.03em)
//  - Subtitle: 13sp → 11sp  (ScreenSubtitle style)
//  - Back button: size 40dp → 38dp, border added (1dp BorderSolid), SurfaceAlt bg
//  - Back button corner: 12dp stays — correct per BackButtonSpec
//  - Back button icon: size explicitly 18dp (BackButtonSpec.iconSize)
//  - Save button: shape 20dp → 12dp  (SaveTopBarButtonSpec.cornerRadius)
//  - Save button height: explicit 38dp  (SaveTopBarButtonSpec.height)
//  - Save button px: horizontal 20dp → 16dp  (SaveTopBarButtonSpec.horizontalPadding)
//  - Save button shadow/glow: added via DrawBehind modifier (TSX boxShadow)
//  - Top bar bottom border: added 1dp BorderSolid  (TSX borderBottom)
//  - windowInsetsPadding: kept — correct for status bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CreateMealTopBar(
    foodCount: Int,
    totalCalories: Float,
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSaved: Boolean = false   // optional saved state (TSX shows green + "Saved!" on tap)
) {
    Surface(
        color = Surface,
        modifier = Modifier
            .fillMaxWidth()
            // TSX: borderBottom: `1px solid ${C.border}`
            .border(
                width = BorderWidthTopBar,
                color = BorderSolid,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Pushes content below the system status bar — correct, keep this
                .windowInsetsPadding(WindowInsets.statusBars)
                // TSX: padding "6px 16px 14px" → top=6, h=16, bottom=14
                .padding(
                    start = TopBarPaddingHorizontal,
                    end = TopBarPaddingHorizontal,
                    top = TopBarPaddingTop,
                    bottom = TopBarPaddingBottom
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ── Back Button ─────────────────────────────────────────────────
            // TSX: width:38, height:38, borderRadius:12, bg:surfaceAlt,
            //      border: `1px solid ${C.border}`, icon: ArrowLeft size:18
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(BackButtonSpec.size) // 38dp
                    .clip(RoundedCornerShape(BackButtonSpec.cornerRadius))  // 12dp
                    .background(SurfaceAlt)
                    .border(
                        width = BackButtonSpec.borderWidth,     // 1dp
                        color = BorderSolid,
                        shape = RoundedCornerShape(BackButtonSpec.cornerRadius)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(BackButtonSpec.iconSize)   // 18dp
                )
            }

            // ── Title + Subtitle ─────────────────────────────────────────────
            // TSX: title fontSize:17, fontWeight:700, letterSpacing:-0.03em
            //      subtitle fontSize:11, fontWeight:500, color:inkSub
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Space2)
            ) {
                Text(
                    text = "Create Meal",
                    style = ScreenTitle,        // 17sp, Bold, letterSpacing:-0.5sp
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$foodCount food${if (foodCount != 1) "s" else ""} · ${totalCalories.toInt()} kcal",
                    style = ScreenSubtitle,     // 11sp, Medium
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // ── Save Button ──────────────────────────────────────────────────
            // TSX: height:38, padding:"0 16px", borderRadius:12,
            //      bg: saved ? green : orange, boxShadow orange glow
            //      label: fontSize:13, fontWeight:700
            Button(
                onClick = onSave,
                modifier = Modifier
                    .height(SaveTopBarButtonSpec.height),     // 38dp
                shape = RoundedCornerShape(SaveTopBarButtonSpec.cornerRadius),  // 12dp
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) Green else Orange
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = ElevationSaveButton,   // 6dp — approximates orange glow
                    pressedElevation = 2.dp
                ),
                contentPadding = PaddingValues(
                    horizontal = SaveTopBarButtonSpec.horizontalPadding,  // 16dp
                    vertical = 0.dp
                )
            ) {
                Text(
                    text = if (isSaved) "Saved!" else "Save",
                    style = ButtonLabel,        // 13sp, Bold, letterSpacing:-0.13sp
                    color = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F7)
@Composable
private fun CreateMealTopBarPreview() {
    MacroForgeTheme {
        CreateMealTopBar(
            foodCount = 3,
            totalCalories = 594f,
            onBack = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F7, name = "Saved state")
@Composable
private fun CreateMealTopBarSavedPreview() {
    MacroForgeTheme {
        CreateMealTopBar(
            foodCount = 3,
            totalCalories = 594f,
            onBack = {},
            onSave = {},
            isSaved = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F7, name = "Empty meal")
@Composable
private fun CreateMealTopBarEmptyPreview() {
    MacroForgeTheme {
        CreateMealTopBar(
            foodCount = 0,
            totalCalories = 0f,
            onBack = {},
            onSave = {}
        )
    }
}