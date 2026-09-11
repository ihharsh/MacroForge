package com.yourname.macroforge.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.macroforge.R

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — Type.kt
//
// Two font families extracted from TSX:
//   1. Outfit       — all UI labels, names, subtitles, buttons (primary family)
//   2. DM Mono      — all numeric/data values (calories, macros, quantity)
//
// To use custom fonts:
//   1. Download Outfit from fonts.google.com/specimen/Outfit
//   2. Download DM Mono from fonts.google.com/specimen/DM+Mono
//   3. Place .ttf files in app/src/main/res/font/
//      → outfit_regular.ttf, outfit_medium.ttf, outfit_semibold.ttf, outfit_bold.ttf
//      → dm_mono_regular.ttf, dm_mono_medium.ttf
//
// Until fonts are added, Compose will fall back to the system default (Roboto).
// ─────────────────────────────────────────────────────────────────────────────

val OutfitFamily = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold),
)

val DmMonoFamily = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
)

// ─────────────────────────────────────────────────────────────────────────────
// Text Styles
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Screen title — "Create Meal"
 * Source: fontSize:17, fontWeight:700, letterSpacing:-0.03em
 */
val ScreenTitle = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 17.sp,
    letterSpacing = (-0.5).sp,
    lineHeight = 22.sp,
)

/**
 * Screen subtitle — "3 foods · 594 kcal"
 * Source: fontSize:11, fontWeight:500, color:inkSub
 */
val ScreenSubtitle = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    letterSpacing = 0.sp,
    lineHeight = 14.sp,
)

/**
 * Food card name — "Chicken Breast", "Brown Rice"
 * Source: fontSize:13, fontWeight:700, letterSpacing:-0.02em
 */
val FoodCardName = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    letterSpacing = (-0.26).sp,
    lineHeight = 16.sp,
)

/**
 * Food card subtitle — "Raw, boneless · per 100g"
 * Source: fontSize:10, fontWeight:500, color:inkSub
 */
val FoodCardSubtitle = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 0.sp,
    lineHeight = 13.sp,
)

/**
 * Quantity input number — "150", "180", "100"
 * Source: DM Mono, fontSize:13, fontWeight:500, textAlign:right
 */
val QuantityInput = TextStyle(
    fontFamily = DmMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    letterSpacing = (-0.13).sp,
    lineHeight = 16.sp,
)

/**
 * Quantity unit suffix — "g", "ml", "scoop"
 * Source: fontSize:10, fontWeight:700, lowercase, color:inkGhost
 */
val QuantityUnit = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    letterSpacing = 0.2.sp,
    lineHeight = 12.sp,
)

/**
 * Macro chip value — "248", "46.5g", "0g", "5.4g"
 * Source: DM Mono, fontSize:13, fontWeight:500
 */
val MacroChipValue = TextStyle(
    fontFamily = DmMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    letterSpacing = 0.sp,
    lineHeight = 16.sp,
)

/**
 * Macro chip label — "KCAL", "PROTEIN", "CARBS", "FAT"
 * Source: Outfit, fontSize:9, fontWeight:600, letterSpacing:0.06em, uppercase
 */
val MacroChipLabel = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 9.sp,
    letterSpacing = 0.54.sp,   // 0.06em × 9sp
    lineHeight = 11.sp,
)

/**
 * Section header label — "ADDED FOODS", "MEAL TOTAL"
 * Source: Outfit, fontSize:11, fontWeight:700, letterSpacing:0.08em, uppercase
 */
val SectionHeader = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    letterSpacing = 0.88.sp,   // 0.08em × 11sp
    lineHeight = 14.sp,
)

/**
 * Section item count — "3 items"
 * Source: Outfit, fontSize:11, fontWeight:600, color:inkSub
 */
val SectionCount = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    letterSpacing = 0.sp,
    lineHeight = 14.sp,
)

/**
 * Search placeholder — "Search foods..."
 * Source: Outfit, fontSize:14, fontWeight:500
 */
val SearchPlaceholder = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    letterSpacing = (-0.14).sp,
    lineHeight = 18.sp,
)

/**
 * Save button label — "Save"
 * Source: Outfit, fontSize:13, fontWeight:700, color:white
 */
val ButtonLabel = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    letterSpacing = (-0.13).sp,
    lineHeight = 16.sp,
)

/**
 * Save Meal bottom button label — "Save Meal"
 * Source: Outfit, fontSize:15, fontWeight:700, color:white
 */
val ButtonLabelLarge = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    letterSpacing = (-0.3).sp,
    lineHeight = 20.sp,
)

/**
 * Donut ring center calorie number — "594"
 * Source: DM Mono, fontSize:15, fontWeight:500, letterSpacing:-0.02em
 */
val DonutCenterValue = TextStyle(
    fontFamily = DmMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    letterSpacing = (-0.3).sp,
    lineHeight = 18.sp,
)

/**
 * Donut ring center label — "kcal"
 * Source: Outfit, fontSize:8, fontWeight:700, letterSpacing:0.06em, uppercase
 */
val DonutCenterLabel = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 8.sp,
    letterSpacing = 0.48.sp,
    lineHeight = 10.sp,
)

/**
 * Macro stat tile value — "63.8", "43.1", "17"
 * Source: DM Mono, fontSize:16, fontWeight:500, letterSpacing:-0.03em
 */
val MacroTileValue = TextStyle(
    fontFamily = DmMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    letterSpacing = (-0.48).sp,
    lineHeight = 20.sp,
)

/**
 * Macro stat tile label — "G PROT", "G CARB", "G FAT"
 * Source: Outfit, fontSize:9, fontWeight:700, letterSpacing:0.05em, uppercase
 */
val MacroTileLabel = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 9.sp,
    letterSpacing = 0.45.sp,
    lineHeight = 11.sp,
)

/**
 * "Nutrition detail" tappable link in footer
 * Source: Outfit, fontSize:11, fontWeight:600, color:orange
 */
val FooterLink = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    letterSpacing = 0.sp,
    lineHeight = 14.sp,
)

/**
 * Search result food name — dropdown
 * Source: Outfit, fontSize:13, fontWeight:600, letterSpacing:-0.01em
 */
val SearchResultName = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    letterSpacing = (-0.13).sp,
    lineHeight = 16.sp,
)

/**
 * Search result meta — "per 100g · 165 kcal · 31g P"
 * Source: Outfit/DM Mono mix, fontSize:10, fontWeight:500
 */
val SearchResultMeta = TextStyle(
    fontFamily = OutfitFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 0.sp,
    lineHeight = 13.sp,
)

// ─────────────────────────────────────────────────────────────────────────────
// Material 3 Typography mapping
// Map MacroForge custom styles into M3 Typography slots for use with MaterialTheme
// ─────────────────────────────────────────────────────────────────────────────
val MacroForgeTypography = Typography(
    titleMedium = ScreenTitle,
    bodyMedium = SearchPlaceholder,
    bodySmall = FoodCardSubtitle,
    labelSmall = MacroChipLabel,
    labelMedium = SectionHeader,
)