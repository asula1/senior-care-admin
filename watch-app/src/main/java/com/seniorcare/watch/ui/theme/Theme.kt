package com.seniorcare.watch.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProvideTextStyle
import androidx.wear.compose.material.Typography

// 노인 친화적 색상 - 고대비, 읽기 쉬운 색상
private val SeniorCareColorPalette = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF4C84FF), // 파란색
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF2855B7),
    secondary = androidx.compose.ui.graphics.Color(0xFFFF5252), // 빨간색 (경고, 비상용)
    secondaryVariant = androidx.compose.ui.graphics.Color(0xFFD10000),
    error = androidx.compose.ui.graphics.Color(0xFFFF5252),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onError = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF1C1C1C),
    onSurface = androidx.compose.ui.graphics.Color.White
)

// 노인 친화적 타이포그래피 - 큰 글씨, 읽기 쉬운 폰트
private val SeniorCareTypography = Typography(
    body1 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(16)
    ),
    button = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(18)
    ),
    caption1 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(14)
    ),
    caption2 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(12)
    ),
    title1 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(24)
    ),
    title2 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(20)
    ),
    title3 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = androidx.compose.ui.unit.sp(18)
    )
)

@Composable
fun SeniorCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = SeniorCareColorPalette,
        typography = SeniorCareTypography,
        content = {
            ProvideTextStyle(
                value = MaterialTheme.typography.body1,
                content = content
            )
        }
    )
}
