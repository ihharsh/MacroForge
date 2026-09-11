package com.example.macroforge.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — Shapes.kt
// Source: Extracted from TSX borderRadius values
// ─────────────────────────────────────────────────────────────────────────────

// ── Raw Radius Values ─────────────────────────────────────────────────────────

/** 9dp — Trash/Add button corners, quantity pill corners */
val RadiusXSmall = 9.dp

/** 10dp — Macro chip inner tiles (estimated from screenshot) */
val RadiusSmall = 10.dp

/** 12dp — Back button, Save button in top bar */
val RadiusMedium = 12.dp

/** 14dp — Macro chips inside food card (macroChip borderRadius:10 in TSX → slightly more visually) */
val RadiusMacroChip = 10.dp

/** 16dp — Search bar field, Save Meal bottom button, dropdown container */
val RadiusLarge = 16.dp

/** 20dp — Food entry cards */
val RadiusXLarge = 20.dp

/** 28dp — Bottom panel top corners (footer panel) */
val RadiusBottomPanel = 28.dp

/** Full pill — anything needing fully rounded ends */
val RadiusPill = 999.dp

// ── Shape Objects ─────────────────────────────────────────────────────────────

/** Food entry card */
val FoodCardShape = RoundedCornerShape(RadiusXLarge)

/** Search bar / text field */
val SearchBarShape = RoundedCornerShape(RadiusLarge)

/** Search autocomplete dropdown — bottom rounded only (connects to search bar top) */
val DropdownShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = RadiusLarge,
    bottomEnd = RadiusLarge
)

/** Macro chip inside food card */
val MacroChipShape = RoundedCornerShape(RadiusMacroChip)

/** Macro stat tile in footer */
val MacroTileShape = RoundedCornerShape(RadiusLarge - 2.dp)  // 14dp

/** Footer / bottom panel */
val FooterShape = RoundedCornerShape(
    topStart = RadiusBottomPanel,
    topEnd = RadiusBottomPanel,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/** Save Meal bottom button */
val SaveMealButtonShape = RoundedCornerShape(RadiusLarge)

/** Top bar "Save" button */
val SaveButtonShape = RoundedCornerShape(RadiusMedium)

/** Back button (icon button) */
val BackButtonShape = RoundedCornerShape(RadiusMedium)

/** Trash button */
val TrashButtonShape = RoundedCornerShape(RadiusXSmall)

/** Add (+) button in search results */
val AddButtonShape = RoundedCornerShape(RadiusXSmall)

/** Quantity input pill container */
val QuantityPillShape = RoundedCornerShape(RadiusXSmall + 1.dp)  // 10dp

/** Small clear (X) button inside search bar */
val ClearButtonShape = RoundedCornerShape(8.dp)

// ── Material 3 Shapes mapping ─────────────────────────────────────────────────
val MacroForgeShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusXSmall),
    small = RoundedCornerShape(RadiusSmall),
    medium = RoundedCornerShape(RadiusMedium),
    large = RoundedCornerShape(RadiusLarge),
    extraLarge = RoundedCornerShape(RadiusXLarge),
)