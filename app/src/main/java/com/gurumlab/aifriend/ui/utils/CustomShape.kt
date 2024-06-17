package com.gurumlab.aifriend.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

object CustomShape {

    @Composable
    fun CustomTriangle(
        modifier: Modifier = Modifier,
        color: Color,
        firstPoint: Offset,
        secondPoint: Offset,
        thirdPoint: Offset
    ) {
        val path = Path().apply {
            moveTo(firstPoint.x, firstPoint.y)
            lineTo(secondPoint.x, secondPoint.y)
            lineTo(thirdPoint.x, thirdPoint.y)
            close()
        }

        Canvas(modifier = modifier) {
            drawPath(
                path = path,
                color = color,
            )
        }
    }

    @Composable
    fun CustomRectangle(
        modifier: Modifier = Modifier,
        topLeft: Offset,
        topRight: Offset,
        radius: CornerRadius,
        size: Offset,
        color: Color
    ) {
        Canvas(modifier = modifier) {
            drawCustomRect(
                topLeft = topLeft,
                topRight = topRight,
                radius = radius,
                size = size,
                color = color
            )
        }
    }

    data class CornerRadius(
        val topLeft: Float,
        val topRight: Float,
        val bottomRight: Float,
        val bottomLeft: Float
    )

    private fun DrawScope.drawCustomRect(
        topLeft: Offset,
        topRight: Offset,
        size: Offset,
        radius: CornerRadius,
        color: Color
    ) {
        val path = Path().apply {
            moveTo(topLeft.x + radius.topLeft, topLeft.y)

            lineTo(topRight.x - radius.topRight, topLeft.y)
            arcTo(
                rect = Rect(
                    topRight.x - 2 * radius.topRight,
                    topRight.y,
                    topRight.x,
                    topRight.y + 2 * radius.topRight
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(topRight.x, topLeft.y + size.y - radius.bottomRight)
            arcTo(
                rect = Rect(
                    topRight.x - 2 * radius.bottomRight,
                    topLeft.y + size.y - 2 * radius.bottomRight,
                    topRight.x,
                    topLeft.y + size.y
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(topLeft.x + radius.bottomLeft, topLeft.y + size.y)
            arcTo(
                rect = Rect(
                    topLeft.x,
                    topLeft.y + size.y - 2 * radius.bottomLeft,
                    topLeft.x + 2 * radius.bottomLeft,
                    topLeft.y + size.y
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(topLeft.x, topLeft.y + radius.topLeft)
            arcTo(
                rect = Rect(
                    topLeft.x,
                    topLeft.y,
                    topLeft.x + 2 * radius.topLeft,
                    topLeft.y + 2 * radius.topLeft
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }

        drawPath(path = path, color = color)
    }
}