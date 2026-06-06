package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

data class StrokePath(
    val points: List<Offset>,
    val color: Color = Color(0xFF5D4037), // Matches mouse brown
    val strokeWidth: Float = 8f
)

@Composable
fun CheeseScratchpad(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFF59D), // Swiss yellow
    holeColor: Color = Color(0xFFFBC02D).copy(alpha = 0.4f), // Indented cheese cheese hole
    onDrawingStatusChanged: (Boolean) -> Unit = {}
) {
    var paths = remember { mutableStateListOf<StrokePath>() }
    var currentPointList = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🧀 Jerry's Scratchpad (Doodle Here!)",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF5D4037)
            )

            Button(
                onClick = {
                    paths.clear()
                    currentPointList.clear()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD84315), // Hot orange-red
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("clear_scratchpad_button")
            ) {
                Text("Bite & Clear!", style = MaterialTheme.typography.labelSmall)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onDrawingStatusChanged(true)
                            currentPointList.clear()
                            currentPointList.add(offset)
                        },
                        onDragEnd = {
                            if (currentPointList.isNotEmpty()) {
                                paths.add(StrokePath(currentPointList.toList()))
                            }
                            currentPointList.clear()
                            onDrawingStatusChanged(false)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentPointList.add(change.position)
                        }
                    )
                }
        ) {
            // Background cheese holes & Drawing layers
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw decorative cheese holes in the canvas background with soft transparency
                drawCircle(color = holeColor, radius = 25.dp.toPx(), center = Offset(w * 0.15f, h * 0.25f))
                drawCircle(color = holeColor, radius = 35.dp.toPx(), center = Offset(w * 0.82f, h * 0.70f))
                drawCircle(color = holeColor, radius = 18.dp.toPx(), center = Offset(w * 0.45f, h * 0.85f))
                drawCircle(color = holeColor, radius = 14.dp.toPx(), center = Offset(w * 0.88f, h * 0.15f))
                drawCircle(color = holeColor, radius = 20.dp.toPx(), center = Offset(w * 0.10f, h * 0.80f))

                // Draw committed paths
                paths.forEach { strokePath ->
                    if (strokePath.points.size > 1) {
                        val path = Path().apply {
                            moveTo(strokePath.points.first().x, strokePath.points.first().y)
                            for (i in 1 until strokePath.points.size) {
                                lineTo(strokePath.points[i].x, strokePath.points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = strokePath.color,
                            style = Stroke(
                                width = strokePath.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Draw active path
                if (currentPointList.size > 1) {
                    val activePath = Path().apply {
                        moveTo(currentPointList.first().x, currentPointList.first().y)
                        for (i in 1 until currentPointList.size) {
                            lineTo(currentPointList[i].x, currentPointList[i].y)
                        }
                    }
                    drawPath(
                        path = activePath,
                        color = Color(0xFF5D4037),
                        style = Stroke(
                            width = 10f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (paths.isEmpty() && currentPointList.isEmpty()) {
                Text(
                    text = "Grab a nibble and write here with your finger!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
