package com.mostafa.majiddelbandam.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mostafa.majiddelbandam.R

val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_bold, FontWeight.Bold)
)

private fun TextStyle.vazir(weight: FontWeight? = null): TextStyle = copy(
    fontFamily = Vazirmatn,
    fontWeight = weight ?: fontWeight
)

val MajidDelbandamTypography = Typography().run {
    copy(
        displayLarge = displayLarge.vazir(FontWeight.Bold).copy(fontSize = 40.sp, lineHeight = 48.sp),
        displayMedium = displayMedium.vazir(FontWeight.Bold),
        displaySmall = displaySmall.vazir(FontWeight.Bold),
        headlineLarge = headlineLarge.vazir(FontWeight.Bold).copy(fontSize = 30.sp, lineHeight = 38.sp),
        headlineMedium = headlineMedium.vazir(FontWeight.Bold).copy(fontSize = 24.sp, lineHeight = 32.sp),
        headlineSmall = headlineSmall.vazir(FontWeight.Bold),
        titleLarge = titleLarge.vazir(FontWeight.Bold).copy(fontSize = 20.sp),
        titleMedium = titleMedium.vazir(FontWeight.Medium).copy(fontSize = 16.sp),
        titleSmall = titleSmall.vazir(FontWeight.Medium),
        bodyLarge = bodyLarge.vazir(FontWeight.Normal).copy(fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = bodyMedium.vazir(FontWeight.Normal).copy(fontSize = 14.sp, lineHeight = 22.sp),
        bodySmall = bodySmall.vazir(FontWeight.Normal),
        labelLarge = labelLarge.vazir(FontWeight.Medium).copy(fontSize = 14.sp),
        labelMedium = labelMedium.vazir(FontWeight.Medium),
        labelSmall = labelSmall.vazir(FontWeight.Medium)
    )
}
