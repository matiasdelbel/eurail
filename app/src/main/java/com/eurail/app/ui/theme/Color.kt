package com.eurail.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF1565C0)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFD1E4FF)
private val OnPrimaryContainer = Color(0xFF001D36)

private val Secondary = Color(0xFF535F70)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFD7E3F8)
private val OnSecondaryContainer = Color(0xFF101C2B)

private val Tertiary = Color(0xFF6B5778)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFF3DAFF)
private val OnTertiaryContainer = Color(0xFF251431)

private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)

private val Background = Color(0xFFF8F9FF)
private val OnBackground = Color(0xFF191C20)
private val Surface = Color(0xFFF8F9FF)
private val OnSurface = Color(0xFF191C20)
private val SurfaceVariant = Color(0xFFDFE2EB)
private val OnSurfaceVariant = Color(0xFF43474E)

private val Outline = Color(0xFF73777F)
private val OutlineVariant = Color(0xFFC3C6CF)

private val PrimaryDark = Color(0xFF9ECAFF)
private val OnPrimaryDark = Color(0xFF003258)
private val PrimaryContainerDark = Color(0xFF00497D)
private val OnPrimaryContainerDark = Color(0xFFD1E4FF)

private val SecondaryDark = Color(0xFFBBC7DB)
private val OnSecondaryDark = Color(0xFF253140)
private val SecondaryContainerDark = Color(0xFF3C4858)
private val OnSecondaryContainerDark = Color(0xFFD7E3F8)

private val TertiaryDark = Color(0xFFD6BEE4)
private val OnTertiaryDark = Color(0xFF3B2948)
private val TertiaryContainerDark = Color(0xFF523F5F)
private val OnTertiaryContainerDark = Color(0xFFF3DAFF)

private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD6)

private val BackgroundDark = Color(0xFF111318)
private val OnBackgroundDark = Color(0xFFE2E2E9)
private val SurfaceDark = Color(0xFF111318)
private val OnSurfaceDark = Color(0xFFE2E2E9)
private val SurfaceVariantDark = Color(0xFF43474E)
private val OnSurfaceVariantDark = Color(0xFFC3C6CF)

private val OutlineDark = Color(0xFF8D9199)
private val OutlineVariantDark = Color(0xFF43474E)

val OfflineYellow = Color(0xFFFFC107)
val OfflineYellowDark = Color(0xFFFFD54F)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)