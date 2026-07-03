package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LuminaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val ratio = size / 72.dp
    val cardSize = 54.dp * ratio
    val offsetVal = 4.dp * ratio
    val iconSize = 32.dp * ratio
    val bulletSize = 4.dp * ratio
    val bulletGap = 2.dp * ratio
    val bulletTextHeight = 2.dp * ratio
    val roundedCorner = 16.dp * ratio

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Back card (Purple)
        Box(
            modifier = Modifier
                .offset(x = offsetVal, y = offsetVal)
                .size(cardSize)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFB28CFF), Color(0xFF7C3AED))
                    ),
                    shape = RoundedCornerShape(roundedCorner)
                )
        )
        // Foreground card (Peach-Yellow)
        Box(
            modifier = Modifier
                .size(cardSize)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE8CC), Color(0xFFFFD8A8))
                    ),
                    shape = RoundedCornerShape(roundedCorner)
                )
                .padding(8.dp * ratio)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                // 3 small lines with bullets
                repeat(3) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = bulletGap)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(bulletSize)
                                .background(Color(0xFF7C3AED), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp * ratio))
                        Box(
                            modifier = Modifier
                                .height(bulletTextHeight)
                                .width(if (it == 2) 12.dp * ratio else 18.dp * ratio)
                                .background(Color(0xFF7C3AED).copy(alpha = 0.7f), RoundedCornerShape(1.dp * ratio))
                        )
                    }
                }
            }
        }
        // Feather overlay (stylized quill pen with orange-pink-purple gradient)
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .offset(x = 10.dp * ratio, y = (-2).dp * ratio)
                .size(iconSize)
                .graphicsLayer(
                    rotationZ = -15f
                )
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFB923C), Color(0xFFEC4899), Color(0xFF7C3AED))
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                    }
                }
        )
    }
}
