package com.example.oryon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.oryon.R
import androidx.compose.ui.text.googlefonts.Font



// 1. Definiere den GoogleFont Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs // Zertifikate für die Verifizierung
)

// 2. Definiere den Namen der Schriftart, wie er bei Google Fonts bekannt ist
val firaSansFontName = GoogleFont("Fira Sans")

// 3. Erstelle die FontFamily mit den gewünschten Schriftschnitten
val FiraSansFontFamily = FontFamily(
    Font(googleFont = firaSansFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = firaSansFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = firaSansFontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = firaSansFontName, fontProvider = provider, weight = FontWeight.Bold, style = FontStyle.Italic)
    // Füge weitere Schriftschnitte hinzu, falls benötigt (z.B. Italic)
    // Font(googleFont = firaSansFontName, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic)
)


// Set of Material typography styles to start with
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FiraSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 33.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FiraSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 29.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FiraSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

)