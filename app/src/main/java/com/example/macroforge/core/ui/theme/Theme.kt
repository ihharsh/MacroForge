package com.yourname.macroforge.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// ─────────────────────────────────────────────────────────────────────────────
// MacroForge Design System — Theme.kt
// Wires Color.kt + Type.kt + Shapes.kt into MaterialTheme
// ─────────────────────────────────────────────────────────────────────────────

private val MacroForgeLightColorScheme = lightColorScheme(
    primary             = Orange,
    onPrimary           = TextOnPrimary,
    primaryContainer    = OrangeBg,
    onPrimaryContainer  = Orange,

    secondary           = Blue,
    onSecondary         = TextOnPrimary,
    secondaryContainer  = BlueBg,
    onSecondaryContainer = Blue,

    tertiary            = Green,
    onTertiary          = TextOnPrimary,
    tertiaryContainer   = GreenBg,
    onTertiaryContainer = Green,

    error               = Red,
    onError             = TextOnPrimary,
    errorContainer      = RedBg,
    onErrorContainer    = Red,

    background          = Background,
    onBackground        = TextPrimary,

    surface             = Surface,
    onSurface           = TextPrimary,
    surfaceVariant      = SurfaceAlt,
    onSurfaceVariant    = TextSecondary,

    outline             = BorderSolid,
    outlineVariant      = BorderSolid,
)

@Composable
fun MacroForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MacroForgeLightColorScheme,
        typography  = MacroForgeTypography,
        shapes      = MacroForgeShapes,
        content     = content
    )
}