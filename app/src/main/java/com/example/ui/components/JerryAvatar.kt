package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class JerryExpression {
    IDLE,
    THINKING,
    HAPPY,
    ENCOURAGING
}

@Composable
fun JerryAvatar(
    expression: JerryExpression,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    // Endless bouncing/breathing animation for visual life
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breatheDy by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_y"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val centerY = h * 0.5f + breatheDy

        // Color Palette
        val brownFur = Color(0xFF8D6E63)      // Main fur
        val darkBrownFur = Color(0xFF5D4037)  // Shadow fur
        val innerEarPink = Color(0xFFFF8A80)  // Inner ears
        val muzzleCream = Color(0xFFE0F7FA)   // Light snout
        val nosePink = Color(0xFFFF4081)      // Cute nose
        val blackInput = Color(0xFF212121)

        // 1. Draw Ears
        // Left Ear
        drawCircle(
            color = brownFur,
            radius = w * 0.28f,
            center = Offset(w * 0.25f, h * 0.28f + breatheDy)
        )
        drawCircle(
            color = innerEarPink,
            radius = w * 0.18f,
            center = Offset(w * 0.25f, h * 0.28f + breatheDy)
        )

        // Right Ear
        drawCircle(
            color = brownFur,
            radius = w * 0.28f,
            center = Offset(w * 0.75f, h * 0.28f + breatheDy)
        )
        drawCircle(
            color = innerEarPink,
            radius = w * 0.18f,
            center = Offset(w * 0.75f, h * 0.28f + breatheDy)
        )

        // Head shadow
        drawCircle(
            color = darkBrownFur,
            radius = w * 0.35f,
            center = Offset(w * 0.5f, centerY + 3f)
        )

        // 2. Draw Main Head Base (Oval-ish circle)
        drawCircle(
            color = brownFur,
            radius = w * 0.34f,
            center = Offset(w * 0.5f, centerY)
        )

        // 3. Cheeks / Snout Base (Two overlapping soft cream circles)
        drawCircle(
            color = Color(0xFFFFF9C4), // Creamy cheese color
            radius = w * 0.13f,
            center = Offset(w * 0.42f, centerY + h * 0.13f)
        )
        drawCircle(
            color = Color(0xFFFFF9C4),
            radius = w * 0.13f,
            center = Offset(w * 0.58f, centerY + h * 0.13f)
        )

        // 4. Eyes
        val leftEyeCenter = Offset(w * 0.40f, centerY - h * 0.05f)
        val rightEyeCenter = Offset(w * 0.60f, centerY - h * 0.05f)

        // Eye whites
        drawOval(
            color = Color.White,
            topLeft = Offset(leftEyeCenter.x - w * 0.07f, leftEyeCenter.y - h * 0.11f),
            size = Size(w * 0.14f, h * 0.22f)
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(rightEyeCenter.x - w * 0.07f, rightEyeCenter.y - h * 0.11f),
            size = Size(w * 0.14f, h * 0.22f)
        )

        // Pupils & Glints based on Expression
        when (expression) {
            JerryExpression.THINKING -> {
                // Pupils look upwards and slightly inward (curious, pondering look)
                drawCircle(
                    color = blackInput,
                    radius = w * 0.045f,
                    center = Offset(leftEyeCenter.x + w * 0.02f, leftEyeCenter.y - h * 0.04f)
                )
                drawCircle(
                    color = blackInput,
                    radius = w * 0.045f,
                    center = Offset(rightEyeCenter.x - w * 0.02f, rightEyeCenter.y - h * 0.04f)
                )
                // Glints
                drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(leftEyeCenter.x + w * 0.02f, leftEyeCenter.y - h * 0.05f))
                drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(rightEyeCenter.x - w * 0.02f, rightEyeCenter.y - h * 0.05f))

                // Eyebrows slanted quizzically
                val pathLeft = Path().apply {
                    moveTo(leftEyeCenter.x - w * 0.06f, leftEyeCenter.y - h * 0.13f)
                    lineTo(leftEyeCenter.x + w * 0.04f, leftEyeCenter.y - h * 0.15f)
                }
                val pathRight = Path().apply {
                    moveTo(rightEyeCenter.x - w * 0.04f, rightEyeCenter.y - h * 0.16f)
                    lineTo(rightEyeCenter.x + w * 0.06f, rightEyeCenter.y - h * 0.12f)
                }
                drawPath(pathLeft, color = blackInput, style = Stroke(width = w * 0.028f))
                drawPath(pathRight, color = blackInput, style = Stroke(width = w * 0.028f))
            }
            JerryExpression.HAPPY, JerryExpression.ENCOURAGING -> {
                // Pupils look centered, happy sparkling glints
                drawCircle(
                    color = blackInput,
                    radius = w * 0.05f,
                    center = leftEyeCenter
                )
                // Winking or encouraging right eye
                if (expression == JerryExpression.ENCOURAGING) {
                    // Draw a closed happy wink curve for right eye instead of pupil
                    val winkPath = Path().apply {
                        moveTo(rightEyeCenter.x - w * 0.06f, rightEyeCenter.y)
                        quadraticTo(
                            rightEyeCenter.x, rightEyeCenter.y + h * 0.06f,
                            rightEyeCenter.x + w * 0.06f, rightEyeCenter.y
                        )
                    }
                    drawOval(
                        color = brownFur, // cover right eye whites
                        topLeft = Offset(rightEyeCenter.x - w * 0.08f, rightEyeCenter.y - h * 0.12f),
                        size = Size(w * 0.16f, h * 0.24f)
                    )
                    drawPath(winkPath, color = blackInput, style = Stroke(width = w * 0.03f))
                } else {
                    drawCircle(
                        color = blackInput,
                        radius = w * 0.05f,
                        center = rightEyeCenter
                    )
                }

                // White sparkling glints
                drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(leftEyeCenter.x - w * 0.015f, leftEyeCenter.y - h * 0.02f))
                if (expression != JerryExpression.ENCOURAGING) {
                    drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(rightEyeCenter.x - w * 0.015f, rightEyeCenter.y - h * 0.02f))
                }

                // Cheerful raised eyebrows
                val pathLeft = Path().apply {
                    moveTo(leftEyeCenter.x - w * 0.06f, leftEyeCenter.y - h * 0.14f)
                    quadraticTo(leftEyeCenter.x, leftEyeCenter.y - h * 0.17f, leftEyeCenter.x + w * 0.06f, leftEyeCenter.y - h * 0.13f)
                }
                drawPath(pathLeft, color = blackInput, style = Stroke(width = w * 0.025f))

                if (expression != JerryExpression.ENCOURAGING) {
                    val pathRight = Path().apply {
                        moveTo(rightEyeCenter.x - w * 0.06f, rightEyeCenter.y - h * 0.13f)
                        quadraticTo(rightEyeCenter.x, rightEyeCenter.y - h * 0.17f, rightEyeCenter.x + w * 0.06f, rightEyeCenter.y - h * 0.14f)
                    }
                    drawPath(pathRight, color = blackInput, style = Stroke(width = w * 0.025f))
                }
            }
            else -> { // IDLE
                // Regular looking forward
                drawCircle(color = blackInput, radius = w * 0.05f, center = leftEyeCenter)
                drawCircle(color = blackInput, radius = w * 0.05f, center = rightEyeCenter)
                drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(leftEyeCenter.x - w * 0.01f, leftEyeCenter.y - h * 0.015f))
                drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(rightEyeCenter.x - w * 0.01f, rightEyeCenter.y - h * 0.015f))

                // Standard brows
                drawLine(
                    color = blackInput,
                    start = Offset(leftEyeCenter.x - w * 0.05f, leftEyeCenter.y - h * 0.14f),
                    end = Offset(leftEyeCenter.x + w * 0.05f, leftEyeCenter.y - h * 0.12f),
                    strokeWidth = w * 0.025f
                )
                drawLine(
                    color = blackInput,
                    start = Offset(rightEyeCenter.x - w * 0.05f, rightEyeCenter.y - h * 0.12f),
                    end = Offset(rightEyeCenter.x + w * 0.05f, rightEyeCenter.y - h * 0.14f),
                    strokeWidth = w * 0.025f
                )
            }
        }

        // 5. Whiskers (Three thin black strokes expanding left and right)
        // Left Whiskers
        drawLine(color = Color.LightGray, start = Offset(w * 0.35f, centerY + h * 0.13f), end = Offset(w * 0.12f, centerY + h * 0.08f), strokeWidth = w * 0.008f)
        drawLine(color = Color.LightGray, start = Offset(w * 0.35f, centerY + h * 0.15f), end = Offset(w * 0.10f, centerY + h * 0.16f), strokeWidth = w * 0.008f)
        drawLine(color = Color.LightGray, start = Offset(w * 0.35f, centerY + h * 0.17f), end = Offset(w * 0.13f, centerY + h * 0.24f), strokeWidth = w * 0.008f)

        // Right Whiskers
        drawLine(color = Color.LightGray, start = Offset(w * 0.65f, centerY + h * 0.13f), end = Offset(w * 0.88f, centerY + h * 0.08f), strokeWidth = w * 0.008f)
        drawLine(color = Color.LightGray, start = Offset(w * 0.65f, centerY + h * 0.15f), end = Offset(w * 0.90f, centerY + h * 0.16f), strokeWidth = w * 0.008f)
        drawLine(color = Color.LightGray, start = Offset(w * 0.65f, centerY + h * 0.17f), end = Offset(w * 0.87f, centerY + h * 0.24f), strokeWidth = w * 0.008f)

        // 6. Whiskers highlight/dots (little brown sensory points)
        drawCircle(color = Color(0xFFA1887F), radius = w * 0.01f, center = Offset(w * 0.41f, centerY + h * 0.11f))
        drawCircle(color = Color(0xFFA1887F), radius = w * 0.01f, center = Offset(w * 0.44f, centerY + h * 0.15f))
        drawCircle(color = Color(0xFFA1887F), radius = w * 0.01f, center = Offset(w * 0.59f, centerY + h * 0.11f))
        drawCircle(color = Color(0xFFA1887F), radius = w * 0.01f, center = Offset(w * 0.56f, centerY + h * 0.15f))

        // 7. Cute Toothy Smile (Path)
        val mouthPath = Path().apply {
            moveTo(w * 0.44f, centerY + h * 0.16f)
            quadraticTo(w * 0.5f, centerY + h * 0.26f, w * 0.56f, centerY + h * 0.16f)
        }
        // Mouth black backing
        drawPath(mouthPath, color = blackInput)
        // Draw little white buck tooth in the center! (Classic Jerry characteristic!)
        val toothSize = w * 0.05f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.48f, centerY + h * 0.18f),
            size = Size(toothSize, toothSize * 1.2f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // 8. Cute nose at intersection (Centered pink oval)
        drawOval(
            color = nosePink,
            topLeft = Offset(w * 0.46f, centerY + h * 0.07f),
            size = Size(w * 0.08f, h * 0.06f)
        )
    }
}
