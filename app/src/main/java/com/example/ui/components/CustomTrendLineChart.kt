package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EventMetricsLog
import com.example.ui.theme.SoftTeal

@Composable
fun CustomTrendLineChart(points: List<EventMetricsLog>) {
    if (points.size < 2) {
        Box(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Awaiting consecutive metrics to trace path line", color = Color.Gray, fontSize = 12.sp)
        }
        return
    }

    Canvas(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        val width = size.width
        val height = size.height

        val stepX = width / (points.size - 1)
        val maxVal = 5f // Standard slide is 1-5
        val minVal = 1f

        val pointsCoordinates = points.mapIndexed { idx, item ->
            val x = idx * stepX
            // Map value 1..5 to height..0
            val y = height - ((item.value - minVal) / (maxVal - minVal) * height)
            Offset(x, y)
        }

        // 1. Create a path for the stroke
        val strokePath = Path().apply {
            moveTo(pointsCoordinates.first().x, pointsCoordinates.first().y)
            for (i in 1 until pointsCoordinates.size) {
                lineTo(pointsCoordinates[i].x, pointsCoordinates[i].y)
            }
        }

        // 2. Create a path for the filled area underneath the line
        val fillPath = Path().apply {
            moveTo(pointsCoordinates.first().x, pointsCoordinates.first().y)
            for (i in 1 until pointsCoordinates.size) {
                lineTo(pointsCoordinates[i].x, pointsCoordinates[i].y)
            }
            // Close the path along the bottom of the canvas
            lineTo(pointsCoordinates.last().x, height)
            lineTo(pointsCoordinates.first().x, height)
            close()
        }

        // Draw background threshold reference guide line (midpoint 3.0 / Stable)
        val midY = height / 2
        drawLine(
            color = Color.LightGray.copy(alpha = 0.35f),
            start = Offset(0f, midY),
            end = Offset(width, midY),
            strokeWidth = 1.dp.toPx()
        )

        // Draw grid boundaries
        drawLine(
            color = Color.LightGray.copy(alpha = 0.2f),
            start = Offset(0f, 0f),
            end = Offset(width, 0f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.LightGray.copy(alpha = 0.2f),
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 1.dp.toPx()
        )

        // Draw the beautiful gradient area fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    SoftTeal.copy(alpha = 0.35f),
                    SoftTeal.copy(alpha = 0.01f)
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw the stroke line on top of the fill
        drawPath(
            path = strokePath,
            color = SoftTeal,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw circle checkpoints for emphasis
        pointsCoordinates.forEachIndexed { i, offset ->
            drawCircle(
                color = SoftTeal,
                radius = 5.dp.toPx(),
                center = offset
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = offset
            )
        }
    }
}
