package com.example.macroforge.core.ui.theme

import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — Dimens.kt
// Source: Extracted from TSX padding/gap/size values (px mapped to dp at 1:1
// ratio — TSX values on a 393pt canvas already correspond to dp)
// ─────────────────────────────────────────────────────────────────────────────

// ── Base Spacing Scale ────────────────────────────────────────────────────────
val Space2  = 2.dp
val Space4  = 4.dp
val Space5  = 5.dp
val Space6  = 6.dp
val Space7  = 7.dp
val Space8  = 8.dp
val Space9  = 9.dp
val Space10 = 10.dp
val Space11 = 11.dp
val Space12 = 12.dp
val Space14 = 14.dp
val Space16 = 16.dp
val Space18 = 18.dp
val Space20 = 20.dp
val Space22 = 22.dp
val Space24 = 24.dp
val Space28 = 28.dp
val Space32 = 32.dp
val Space40 = 40.dp

// ── Screen / Layout ───────────────────────────────────────────────────────────
/** Horizontal screen edge padding (all sections, search bar, cards) */
val ScreenPaddingHorizontal = 16.dp

/** Top bar: top padding from status bar safe area */
val TopBarPaddingTop = 6.dp

/** Top bar: bottom padding below content */
val TopBarPaddingBottom = 14.dp

/** Top bar: horizontal padding */
val TopBarPaddingHorizontal = 16.dp

// ── Search Bar ────────────────────────────────────────────────────────────────
/** Outer container padding top from content area */
val SearchBarPaddingTop = 14.dp

/** Internal search bar padding (vertical + horizontal inside the field) */
val SearchBarInternalPaddingVertical = 12.dp
val SearchBarInternalPaddingHorizontal = 14.dp

/** Gap between search icon and input text */
val SearchBarIconTextGap = 10.dp

/** Estimated total height of search field */
val SearchBarHeight = 48.dp   // Estimated from screenshot

/** Border width: inactive */
val SearchBorderWidthInactive = 1.5.dp

/** Border width: active/focused (orange) */
val SearchBorderWidthActive = 1.5.dp

/** Focus ring shadow spread */
val SearchFocusRingSpread = 3.dp

// ── Section Header ─────────────────────────────────────────────────────────────
val SectionHeaderPaddingTop    = 18.dp
val SectionHeaderPaddingBottom = 8.dp
val SectionHeaderPaddingHorizontal = 16.dp

// ── Food Entry Cards ───────────────────────────────────────────────────────────
/** Gap between consecutive food cards in the list */
val FoodCardListGap = 7.dp

/** Card internal padding — header row (name + qty + trash) */
val FoodCardHeaderPaddingHorizontal = 12.dp
val FoodCardHeaderPaddingTop        = 11.dp
val FoodCardHeaderPaddingBottom     = 9.dp

/** Card internal padding — macro chips row */
val FoodCardMacroMarginHorizontal = 12.dp
val FoodCardMacroMarginBottom     = 11.dp

/** Gap between food name column and quantity pill */
val FoodCardHeaderGap = 8.dp

/** Card border width */
val FoodCardBorderWidth = 1.5.dp

// ── Quantity Input Pill ───────────────────────────────────────────────────────
val QuantityPillPaddingVertical   = 5.dp
val QuantityPillPaddingHorizontal = 8.dp
val QuantityInputWidth            = 44.dp

// ── Macro Chip Grid ───────────────────────────────────────────────────────────
/** Gap between the 4 macro chips inside each food card */
val MacroChipGridGap = 5.dp

/** Internal padding of each macro chip (vertical only — full width via flex) */
val MacroChipPaddingVertical = 5.dp

// ── Buttons ───────────────────────────────────────────────────────────────────
/** Top bar "Save" button */
val SaveButtonHeight            = 38.dp
val SaveButtonPaddingHorizontal = 16.dp

/** Bottom "Save Meal" primary button */
val SaveMealButtonHeight            = 52.dp
val SaveMealButtonPaddingHorizontal = 16.dp

/** Bottom button: outer container padding */
val SaveMealButtonContainerPaddingHorizontal = 16.dp
val SaveMealButtonContainerPaddingBottom     = 32.dp

/** Gap between icon and text inside buttons */
val ButtonIconTextGap = 8.dp

// ── Back Button (icon button in top bar) ─────────────────────────────────────
val BackButtonSize          = 38.dp
val BackButtonIconSize      = 18.dp

// ── Trash Button ─────────────────────────────────────────────────────────────
val TrashButtonSize     = 28.dp
val TrashButtonIconSize = 12.dp

// ── Add (+) Button in search results ─────────────────────────────────────────
val AddButtonSize     = 28.dp
val AddButtonIconSize = 14.dp

// ── Bottom Panel (Meal Total footer) ─────────────────────────────────────────
val FooterPaddingHorizontal = 16.dp
val FooterPaddingTop        = 16.dp
val FooterPaddingBottom     = 12.dp

/** Gap between donut ring and the 3 macro tiles */
val FooterDonutTilesGap = 12.dp

/** Gap between the 3 macro tiles */
val FooterTileGap = 6.dp

/** Macro tile internal padding */
val FooterTilePaddingVertical   = 10.dp
val FooterTilePaddingHorizontal = 8.dp

/** Gap between macro tile icon and value */
val FooterTileIconValueGap = 2.dp

// ── Donut Ring ────────────────────────────────────────────────────────────────
val DonutSize        = 72.dp
val DonutStrokeWidth = 9.dp   // proportional: 72 * 0.125 ≈ 9dp
val DonutRadius      = 28.dp

// ── Macro Legend Bar (proportion bar below tiles) ─────────────────────────────
val LegendBarHeight  = 4.dp
val LegendBarGap     = 4.dp
val LegendBarMarginTop = 10.dp

// ── Search Autocomplete Dropdown ──────────────────────────────────────────────
val DropdownItemPaddingHorizontal = 14.dp
val DropdownItemPaddingVertical   = 10.dp
val DropdownItemGap               = 10.dp

// ── Icons ─────────────────────────────────────────────────────────────────────
val IconSizeSmall  = 12.dp
val IconSizeMedium = 17.dp
val IconSizeLarge  = 18.dp
val MacroTileIconSize = 13.dp
val ChevronIconSize   = 12.dp