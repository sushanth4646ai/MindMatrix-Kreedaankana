package com.kreedaankana.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Primary Accents
val OrangeAccent = Color(0xFFFF5722)
val DeepOrange = Color(0xFFFF3D00)
val NeonGreen = Color(0xFF39FF14) // Used for status/success
val ElectricBlue = Color(0xFF00F0FF)
val Purple = Color(0xFF8B5CF6)

// Dark Theme Surfaces
val DarkCharcoal = Color(0xFF060A11)
val SurfaceDark = Color(0xFF0D1622)
val SurfaceVariantDark = Color(0xFF1A2737)
val ElevatedSurface = Color(0xFF1E2D3D)
val CardBorder = Color(0xFF2A2A2A)
val OrangeBorder = Color(0xFFFF5722)

// Text Colors
val TextPrimary = Color(0xFFEAF2FF)
val TextSecondary = Color(0xFF93A4BE)
val TextMuted = Color(0xFF6B7F94)

// Status Colors
val ErrorRed = Color(0xFFFF3B30)
val SuccessGreen = Color(0xFF34C759)
val WarningYellow = Color(0xFFFFCC00)

// Glassmorphism
val GlassWhite5 = Color(0x0DFFFFFF)
val GlassWhite10 = Color(0x1AFFFFFF)
val GlassWhite15 = Color(0x26FFFFFF)
val GlassWhite20 = Color(0x33FFFFFF)

// Gradients
val GradientOrange = Brush.linearGradient(listOf(OrangeAccent, DeepOrange))
val GradientGreen = Brush.linearGradient(listOf(NeonGreen, Color(0xFF00C853)))
val GradientBlue = Brush.linearGradient(listOf(ElectricBlue, Color(0xFF0066FF)))
val GradientPurple = Brush.linearGradient(listOf(Purple, Color(0xFF6366F1)))

val GradientDark = Brush.verticalGradient(listOf(
    Color.Transparent,
    DarkCharcoal.copy(alpha = 0.8f),
    DarkCharcoal
))