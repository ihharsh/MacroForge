package com.yourname.macroforge.core.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — Color.kt
// Source: Extracted from TSX source (C object) + confirmed against screenshot
// ─────────────────────────────────────────────────────────────────────────────

// ── Primary / Brand ──────────────────────────────────────────────────────────
/** Main orange CTA, active search border, Save button, section dot, + buttons */
val Orange = Color(0xFFFF5E2C)

/** Mid orange — used for gradient end on Save Meal button */
val OrangeMid = Color(0xFFFF8A5C)

/** Light orange tint — background for calorie macro chips, alert/delete tints */
val OrangeBg = Color(0xFFFFF1EC)

// ── Surface / Background ─────────────────────────────────────────────────────
/** App-level background — light cool gray behind all cards */
val Background = Color(0xFFF0F2F7)

/** Primary surface — top bar, cards, search field, bottom panel */
val Surface = Color(0xFFFFFFFF)

/** Alternate surface — quantity input bg, back button bg, hover states, search X button */
val SurfaceAlt = Color(0xFFF7F8FA)

// ── Text ─────────────────────────────────────────────────────────────────────
/** Primary text — food names, numbers, screen title */
val TextPrimary = Color(0xFF0C0F1A)

/** Secondary text — brand subtitle, section subtitles, "3 foods · 594 kcal" */
val TextSecondary = Color(0xFF6B7280)

/** Ghost/tertiary text — placeholder, macro chip labels, unit suffix */
val TextGhost = Color(0xFFB0B5C3)

/** Text on primary (orange/colored) buttons */
val TextOnPrimary = Color(0xFFFFFFFF)

// ── Border / Divider ─────────────────────────────────────────────────────────
/** Default card border, search border (inactive), quantity input border */
// rgba(12,15,26,0.07) = #0C0F1A at 7% opacity → approximate solid: #ECEEF3
val Border = Color(0x120C0F1A)        // translucent — use with .copy(alpha) if needed
val BorderSolid = Color(0xFFECEEF3)  // solid approximation for Compose borders

// ── Macro Semantic — Protein (Blue) ──────────────────────────────────────────
/** Protein value text, protein donut arc, protein tile icon */
val Blue = Color(0xFF2F7EFF)

/** Protein macro chip / tile background */
val BlueBg = Color(0xFFEBF3FF)

/** Protein label text inside tile ("G PROT") */
val BlueLabel = Color(0xFF93C5FD)

// ── Macro Semantic — Carbs (Amber) ───────────────────────────────────────────
/** Carbs value text, carbs donut arc, carbs tile icon */
val Amber = Color(0xFFF0A500)

/** Carbs macro chip / tile background */
val AmberBg = Color(0xFFFFF8E6)

/** Carbs label text inside tile ("G CARB") */
val AmberLabel = Color(0xFFFCD34D)

// ── Macro Semantic — Fat (Green) ─────────────────────────────────────────────
/** Fat value text, fat donut arc, fat tile icon */
val Green = Color(0xFF12B76A)

/** Fat macro chip / tile background */
val GreenBg = Color(0xFFE8FAF2)

/** Fat label text inside tile ("G FAT") */
val GreenLabel = Color(0xFF6EE7B7)

// ── Semantic — Error / Delete ─────────────────────────────────────────────────
/** Trash icon color */
val Red = Color(0xFFF04438)

/** Trash button background */
val RedBg = Color(0xFFFEF3F2)

// ── Donut Ring Track ─────────────────────────────────────────────────────────
/** Empty/track ring background behind the donut arcs */
val DonutTrack = Color(0xFFECEEF3)

// ── Phone Shell (reference only — not used in app UI) ────────────────────────
// Shell gradient: #D6DCE8 → #C8CDD9 — not needed in Compose theme