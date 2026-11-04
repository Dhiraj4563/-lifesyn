package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// LifeSync Brand Palette
val NavyBackground = Color(0xFF0B1220)
val NavyCard = Color(0xFF17243A)
val NavyCardElevated = Color(0xFF1F304E)
val NavyBorder = Color(0xFF223554)
val NavyBorderSubtle = Color(0xFF19283F)

val PurplePrimary = Color(0xFF8B5CF6)
val PurpleSecondary = Color(0xFF7C3AED)
val PurpleAccent = Color(0xFFA78BFA)
val IndigoAccent = Color(0xFF6366F1)

val BlueAccent = Color(0xFF3B82F6)
val GreenAccent = Color(0xFF10B981)
val OrangeAccent = Color(0xFFF59E0B)
val RedAccent = Color(0xFFEF4444)
val PinkAccent = Color(0xFFEC4899)
val CyanAccent = Color(0xFF06B6D4)

val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

// Light Theme Palette
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF94A3B8)

// Gradients
val PurpleButtonGradient = Brush.horizontalGradient(
    colors = listOf(PurplePrimary, IndigoAccent)
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(NavyCardElevated, NavyCard)
)
