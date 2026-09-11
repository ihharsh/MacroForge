package com.example.macroforge.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — DesignTokens.kt
// Elevation, borders, shadows, and composite component specifications
// Source: TSX boxShadow values + screenshot analysis
// ─────────────────────────────────────────────────────────────────────────────

// ── Elevation ─────────────────────────────────────────────────────────────────
// Note: Compose uses dp for shadow elevation.
// TSX uses layered box-shadows which don't map 1:1 to Compose elevation,
// but these values approximate the visual depth seen in the screenshot.

/** Food entry cards — very subtle lift */
val ElevationCard = 2.dp    // TSX: "0 1px 6px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.03)"

/** Bottom footer panel — visible lift above content */
val ElevationFooter = 8.dp  // TSX: "0 -8px 40px rgba(0,0,0,0.08)"

/** Active search bar — slight lift when focused */
val ElevationSearchActive = 4.dp   // TSX: "0 2px 8px rgba(0,0,0,0.04)" when inactive

/** Search autocomplete dropdown */
val ElevationDropdown = 12.dp  // TSX: "0 12px 32px rgba(0,0,0,0.12)"

/** Save button shadow (orange glow) — use with Compose shadow modifier tint */
val ElevationSaveButton = 6.dp  // TSX: "0 6px 20px {orange}50"

/** Top bar — no visible elevation, uses border-bottom instead */
val ElevationTopBar = 0.dp

// ── Borders ───────────────────────────────────────────────────────────────────

/** Standard thin border on cards, quantity pill, search bar inactive */
val BorderWidthThin = 1.dp      // TSX: quantity pill, food card

/** Standard medium border on search bar + dropdown */
val BorderWidthMedium = 1.5.dp  // TSX: all search/dropdown borders

/** Top bar bottom separator */
val BorderWidthTopBar = 1.dp    // TSX: borderBottom on top bar

// Border color aliases (from Color.kt)
val BorderDefault   get() = Border           // rgba(12,15,26,0.07) — default card/input border
val BorderActive    get() = Orange           // #FF5E2C — focused search bar
val BorderSeparator get() = BorderSolid      // #ECEEF3 — between dropdown items (dividers)

// ── Shadow / Glow Colors ──────────────────────────────────────────────────────
// These are the colored shadows used in TSX — approximate with Compose shadow where possible

/** Orange button glow: Color(0xFFFF5E2C) at ~32% opacity */
val ShadowOrange = Color(0x55FF5E2C)   // #FF5E2C55

/** Search focus ring: Color(0xFFFF5E2C) at ~9% opacity */
val ShadowSearchFocus = Color(0x18FF5E2C)   // #FF5E2C18

/** Green success button glow */
val ShadowGreen = Color(0x5512B76A)

// ── Component Specifications ──────────────────────────────────────────────────

object TopBarSpec {
    val height = 64.dp              // Estimated from screenshot — status bar + bar content
    val paddingHorizontal = 16.dp
    val paddingTop = 6.dp           // TSX: padding top after status bar
    val paddingBottom = 14.dp
    val borderBottomWidth = 1.dp
    val borderColor = Border
}

object SearchBarSpec {
    val height = 48.dp              // Estimated from screenshot
    val cornerRadius = 16.dp
    val paddingVertical = 12.dp
    val paddingHorizontal = 14.dp
    val iconSize = 17.dp
    val borderWidthInactive = 1.5.dp
    val borderWidthActive = 1.5.dp
    val borderColorInactive = Border
    val borderColorActive = Orange
    val clearButtonSize = 22.dp
    val clearButtonCornerRadius = 8.dp
    val clearIconSize = 12.dp
}

object FoodCardSpec {
    val cornerRadius = 20.dp
    val borderWidth = 1.5.dp
    val borderColor = Border
    val headerPaddingHorizontal = 12.dp
    val headerPaddingVertical = 11.dp       // top; 9dp bottom
    val macroRowMarginHorizontal = 12.dp
    val macroRowMarginBottom = 11.dp
    val macroChipGap = 5.dp
    val elevation = 2.dp
}

object QuantityPillSpec {
    val paddingVertical = 5.dp
    val paddingHorizontal = 8.dp
    val cornerRadius = 10.dp
    val borderWidth = 1.dp
    val borderColor = Border
    val inputWidth = 35.dp
    val backgroundColor = SurfaceAlt
}

object TrashButtonSpec {
    val size = 28.dp
    val cornerRadius = 9.dp
    val iconSize = 12.dp
    val backgroundColor = RedBg
    val iconColor = Red
}

object AddButtonSpec {
    val size = 28.dp
    val cornerRadius = 9.dp
    val iconSize = 14.dp
    val backgroundColor = Orange
    val iconColor = Color.White
}

object MacroChipSpec {
    val cornerRadius = 10.dp
    val paddingVertical = 5.dp
    // Width: equal share of card width (flex:1 in TSX → Modifier.weight(1f) in Compose)
    val valueToLabelGap = 1.dp
}

object MacroTileSpec {
    val cornerRadius = 14.dp
    val paddingVertical = 10.dp
    val paddingHorizontal = 8.dp
    val iconSize = 13.dp
    val iconValueGap = 2.dp
    // Width: equal share of remaining row space
}

object DonutSpec {
    val size = 72.dp
    val strokeWidth = 9.dp
    val radius = 28.dp
    val trackColor = DonutTrack
    val startAngleDeg = -90f    // top of circle = 12 o'clock
}

object FooterSpec {
    val topCornerRadius = 28.dp
    val borderTopWidth = 1.5.dp
    val borderColor = Border
    val paddingHorizontal = 16.dp
    val paddingTop = 16.dp
    val totalsAreaPaddingBottom = 12.dp
    val elevation = 8.dp
}

object SaveMealButtonSpec {
    val height = 52.dp
    val cornerRadius = 16.dp
    val paddingHorizontal = 16.dp
    val containerPaddingBottom = 32.dp    // safe area bottom
    val iconSize = 18.dp
    val iconTextGap = 8.dp
    // Gradient: Orange(0°) → 0xFFE8480F (135°)
    val gradientStartColor = Orange
    val gradientEndColor = Color(0xFFE8480F)
    val shadowColor = ShadowOrange
}

object SaveTopBarButtonSpec {
    val height = 38.dp
    val horizontalPadding = 16.dp
    val cornerRadius = 12.dp
    val iconSize = 15.dp
    val iconTextGap = 5.dp
}

object BackButtonSpec {
    val size = 38.dp
    val cornerRadius = 12.dp
    val iconSize = 18.dp
    val backgroundColor = SurfaceAlt
    val borderWidth = 1.dp
    val borderColor = Border
}

object DropdownSpec {
    val topCornerRadius = 0.dp
    val bottomCornerRadius = 16.dp
    val borderWidth = 1.5.dp
    val borderColor = Orange
    val elevation = 12.dp
    val itemPaddingHorizontal = 14.dp
    val itemPaddingVertical = 10.dp
    val itemGap = 10.dp
    val dividerColor = BorderSolid
    val addButtonSize = 28.dp
    val addButtonCornerRadius = 9.dp
}

object LegendBarSpec {
    val height = 4.dp
    val cornerRadius = 4.dp
    val gap = 4.dp
    val marginTop = 10.dp
    // Segments: protein(blue) | carbs(amber) | fat(green)
    // Width proportional to macroGrams (not calories) — matches TSX implementation
}