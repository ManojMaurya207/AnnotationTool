package com.medprimetech.annotationapp.presentation.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.PointFSerializable
import com.medprimetech.annotationapp.domain.model.ToolType
import kotlinx.coroutines.flow.StateFlow
import android.graphics.Paint as AndroidPaint
@Composable
fun DrawingCanvas(
    annotations: List<AnnotationItem>,
    selectedTool: ToolType,
    onDraw: (AnnotationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentFreehandPoints by remember { mutableStateOf<List<PointFSerializable>>(emptyList()) }
    var arrowStart by remember { mutableStateOf<PointFSerializable?>(null) }
    var arrowEnd by remember { mutableStateOf<PointFSerializable?>(null) }

    Canvas(
        modifier = modifier.pointerInput(selectedTool) {
            when (selectedTool) {
                ToolType.FREEHAND -> {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentFreehandPoints = listOf(PointFSerializable(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            val pos = change.position
                            currentFreehandPoints = currentFreehandPoints + PointFSerializable(pos.x, pos.y)
                            change.consume()
                        },
                        onDragEnd = {
                            if (currentFreehandPoints.size > 1) {
                                onDraw(
                                    AnnotationItem.Freehand(
                                        points = currentFreehandPoints,
                                        color = 0xFF000000,
                                        strokeWidth = 6f
                                    )
                                )
                            }
                            currentFreehandPoints = emptyList()
                        },
                        onDragCancel = {
                            currentFreehandPoints = emptyList()
                        }
                    )
                }

                ToolType.ARROW -> {
                    detectDragGestures(
                        onDragStart = { offset ->
                            arrowStart = PointFSerializable(offset.x, offset.y)
                            arrowEnd = null
                        },
                        onDrag = { change, _ ->
                            arrowEnd = PointFSerializable(change.position.x, change.position.y)
                            change.consume()
                        },
                        onDragEnd = {
                            val s = arrowStart
                            val e = arrowEnd
                            if (s != null && e != null) {
                                onDraw(
                                    AnnotationItem.Arrow(
                                        start = s,
                                        end = e,
                                        color = 0xFF000000,
                                        strokeWidth = 6f
                                    )
                                )
                            }
                            arrowStart = null
                            arrowEnd = null
                        },
                        onDragCancel = {
                            arrowStart = null
                            arrowEnd = null
                        }
                    )
                }

                else -> Unit
            }
        }
    ) {
        // 1. Draw saved annotations
        annotations.forEach { ann ->
            when (ann) {
                is AnnotationItem.Freehand -> {
                    val path = Path().apply {
                        ann.points.firstOrNull()?.let { moveTo(it.x, it.y) }
                        ann.points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path = path,
                        color = Color(ann.color.toULong()),
                        style = Stroke(width = ann.strokeWidth)
                    )
                }

                is AnnotationItem.Arrow -> {
                    drawLine(
                        color = Color(ann.color.toULong()),
                        start = Offset(ann.start.x, ann.start.y),
                        end = Offset(ann.end.x, ann.end.y),
                        strokeWidth = ann.strokeWidth
                    )
                }

                is AnnotationItem.Text -> {
                    val paint = AndroidPaint().apply {
                        color = ann.color.toInt()
                        textSize = ann.fontSize
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        ann.text,
                        ann.position.x,
                        ann.position.y,
                        paint
                    )
                }

                else -> Unit
            }
        }

        // 2. Draw current stroke while dragging
        if (currentFreehandPoints.size > 1) {
            val inProgress = Path().apply {
                currentFreehandPoints.firstOrNull()?.let { moveTo(it.x, it.y) }
                currentFreehandPoints.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(inProgress, color = Color.Black, style = Stroke(width = 6f))
        }

        if (arrowStart != null && arrowEnd != null) {
            drawLine(
                color = Color.Black,
                start = Offset(arrowStart!!.x, arrowStart!!.y),
                end = Offset(arrowEnd!!.x, arrowEnd!!.y),
                strokeWidth = 6f
            )
        }
    }
}
