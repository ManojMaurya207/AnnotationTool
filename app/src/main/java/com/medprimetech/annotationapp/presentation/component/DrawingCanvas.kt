package com.medprimetech.annotationapp.presentation.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.AnnotationItem.Arrow
import com.medprimetech.annotationapp.domain.model.AnnotationItem.Eraser
import com.medprimetech.annotationapp.domain.model.AnnotationItem.Freehand
import com.medprimetech.annotationapp.domain.model.AnnotationItem.Text
import com.medprimetech.annotationapp.domain.model.PointFSerializable
import com.medprimetech.annotationapp.domain.model.RectSerializable
import com.medprimetech.annotationapp.domain.model.ShapeType
import com.medprimetech.annotationapp.domain.model.ToolType
import com.medprimetech.annotationapp.presentation.model.DrawingAction
import com.medprimetech.annotationapp.presentation.viewmodel.AnnotationViewModel
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun DrawingCanvas(
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnnotationViewModel,
) {
    val state by viewModel.state.collectAsState()
    // Local state for in-progress strokes
    var currentFreehandPoints by remember { mutableStateOf<List<PointFSerializable>>(emptyList()) }
    var arrowStart by remember { mutableStateOf<PointFSerializable?>(null) }
    var arrowEnd by remember { mutableStateOf<PointFSerializable?>(null) }
    var shapeStart by remember { mutableStateOf<PointFSerializable?>(null) }
    var shapeEnd by remember { mutableStateOf<PointFSerializable?>(null) }
    var currentEraserPoints by remember { mutableStateOf<List<PointFSerializable>>(emptyList()) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .pointerInput(state.selectedTool) {

                when (state.selectedTool) {
                    ToolType.FREEHAND -> detectDragGestures(
                        onDragStart = { offset ->
                            currentFreehandPoints = listOf(PointFSerializable(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            currentFreehandPoints += PointFSerializable(
                                change.position.x,
                                change.position.y
                            )
                            change.consume()
                        },
                        onDragEnd = {
                            if (currentFreehandPoints.size > 1) {
                                onAction(
                                    DrawingAction.AddAnnotation(
                                        Freehand(
                                            points = currentFreehandPoints,
                                            color = state.selectedColor.toColorLong(),
                                            strokeWidth = state.strokeWidth
                                        )
                                    )
                                )
                            }
                            currentFreehandPoints = emptyList()
                        },
                        onDragCancel = { currentFreehandPoints = emptyList() }
                    )

                    ToolType.ARROW -> detectDragGestures(
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
                                onAction(
                                    DrawingAction.AddAnnotation(
                                        Arrow(
                                            s, e,
                                            state.selectedColor.toColorLong(),
                                            state.strokeWidth
                                        )
                                    )
                                )
                            }
                            arrowStart = null
                            arrowEnd = null
                        },
                        onDragCancel = { arrowStart = null; arrowEnd = null }
                    )

                    ToolType.SHAPE -> detectDragGestures(
                        onDragStart = { offset ->
                            shapeStart = PointFSerializable(offset.x, offset.y)
                            shapeEnd = null
                        },
                        onDrag = { change, _ ->
                            shapeEnd = PointFSerializable(change.position.x, change.position.y)
                            change.consume()
                        },
                        onDragEnd = {
                            val s = shapeStart
                            val e = shapeEnd
                            if (s != null && e != null) {
                                val left = min(s.x, e.x)
                                val right = max(s.x, e.x)
                                val top = min(s.y, e.y)
                                val bottom = max(s.y, e.y)

                                onAction(
                                    DrawingAction.AddAnnotation(
                                        AnnotationItem.Shape(
                                            bounds = RectSerializable(left, top, right, bottom),
                                            color = state.selectedColor.toColorLong(),
                                            strokeWidth = state.strokeWidth,
                                            shapeType = state.selectedShape
                                        )
                                    )
                                )
                            }
                            shapeStart = null
                            shapeEnd = null
                        },
                        onDragCancel = { shapeStart = null; shapeEnd = null }
                    )

                    ToolType.TEXT -> {
                        detectTapGestures { tapOffset ->
                                onAction(DrawingAction.RequestText(PointFSerializable(tapOffset.x, tapOffset.y)))
                        }
                    }

                    ToolType.ERASER -> detectDragGestures(
                        onDragStart = { offset ->
                            currentEraserPoints = listOf(PointFSerializable(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            currentEraserPoints += PointFSerializable(
                                change.position.x,
                                change.position.y
                            )
                            change.consume()
                        },
                        onDragEnd = {
                            if (currentEraserPoints.size > 1) {
                                onAction(
                                    DrawingAction.AddAnnotation(
                                        Eraser(
                                            currentEraserPoints,
                                            strokeWidth = state.strokeWidth
                                        )
                                    )
                                )
                            }
                            currentEraserPoints = emptyList()
                        },
                        onDragCancel = { currentEraserPoints = emptyList() }
                    )
                }
            }
    ) {
        // Persisted annotations
        state.annotations.forEach { ann ->
            drawAnnotation(ann,textMeasurer)
        }

        // In-progress previews
        if (currentFreehandPoints.size > 1) {
            val preview = buildSmoothedPath(currentFreehandPoints)
            drawPath(preview, state.selectedColor, style = Stroke(width = state.strokeWidth, cap = StrokeCap.Round))
        }

        if (arrowStart != null && arrowEnd != null) {
            drawLine(
                color = state.selectedColor,
                start = Offset(arrowStart!!.x, arrowStart!!.y),
                end = Offset(arrowEnd!!.x, arrowEnd!!.y),
                strokeWidth = state.strokeWidth,
                cap = StrokeCap.Round,
            )
        }

        if (shapeStart != null && shapeEnd != null) {
            val l = min(shapeStart!!.x, shapeEnd!!.x)
            val r = max(shapeStart!!.x, shapeEnd!!.x)
            val t = min(shapeStart!!.y, shapeEnd!!.y)
            val b = max(shapeStart!!.y, shapeEnd!!.y)
            val rect = Rect(l, t, r, b)

            val radius = min(rect.width, rect.height) / 2f
            val center = rect.center
            when (state.selectedShape) {
                ShapeType.RECTANGLE -> drawRect(
                    state.selectedColor,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(width = state.strokeWidth, cap = StrokeCap.Round)
                )

                ShapeType.CIRCLE -> drawCircle(
                    color = state.selectedColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = state.strokeWidth, cap = StrokeCap.Round),
                )
            }
        }

        if (currentEraserPoints.size > 1) {
            val p = buildSmoothedPath(currentEraserPoints)
            drawPath(
                p,
                Color.Transparent,
                style = Stroke(width = state.strokeWidth, cap = StrokeCap.Round),
                blendMode = BlendMode.Clear
            )
        }

        if (state.pendingTextPosition!=null){
//            drawText(
//                text = state.pendingTextPosition.toString(),
//                color = Color.Black,
//                topLeft = Offset(state.pendingTextPosition.x, state.pendingTextPosition.y)
//
//            )
        }
    }
}

private fun DrawScope.drawAnnotation(ann: AnnotationItem, textMeasurer: TextMeasurer) {

    when (ann) {
        is Freehand -> {
//            Log.d("TextAnnotation", "drawStoredAnnotation: ${ann}")
            val path = buildSmoothedPath(ann.points)
            drawPath(
                path = path,
                color = Color(ann.color.toULong()),
                style = Stroke(width = ann.strokeWidth, cap = StrokeCap.Round)
            )
        }

        is Arrow -> {
            drawArrow(ann)
        }

        is Text -> {

            Log.d("TextAnnotation", "drawStoredAnnotation: ${ann}")
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(ann.text),
                style = TextStyle(
                    color = Color(ann.color.toULong()),
                    fontSize = ann.fontSize.sp,
                    background = Color.Transparent,
                    fontStyle = FontStyle.Italic
                )
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(ann.position.x, ann.position.y)
            )
            
        }

        is AnnotationItem.Shape -> {
            drawShape(ann)
        }

        is Eraser -> {
            if (ann.path.size > 1) {
                val p = buildSmoothedPath(ann.path)
                drawPath(
                    p,
                    Color.Transparent,
                    style = Stroke(width = ann.strokeWidth, cap = StrokeCap.Round),
                    blendMode = BlendMode.Clear
                )
            }
        }

    }
}

fun buildSmoothedPath(points: List<PointFSerializable>, smoothness: Int = 5): Path {
    return Path().apply {
        if (points.isEmpty()) return@apply
        moveTo(points.first().x, points.first().y)
        for (i in 1..points.lastIndex) {
            val from = points[i - 1]
            val to = points[i]
            val dx = abs(from.x - to.x)
            val dy = abs(from.y - to.y)
            if (dx >= smoothness || dy >= smoothness) {
                quadraticTo((from.x + to.x) / 2f, (from.y + to.y) / 2f, to.x, to.y)
            } else {
                lineTo(to.x, to.y)
            }
        }
    }
}

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()

// --- Helpers to keep drawing logic consistent ---
private fun DrawScope.drawArrow(ann: Arrow) {
    drawLine(
        color = Color(ann.color.toULong()),
        start = Offset(ann.start.x, ann.start.y),
        end = Offset(ann.end.x, ann.end.y),
        strokeWidth = ann.strokeWidth,
        cap = StrokeCap.Round
    )

    val arrowSize = 30f
    val angle = 25f
    val dx = ann.start.x - ann.end.x
    val dy = ann.start.y - ann.end.y
    val lineAngle = atan2(dy, dx)

    val angle1 = lineAngle + angle.toRadians()
    val angle2 = lineAngle - angle.toRadians()

    val x1 = ann.end.x + arrowSize * cos(angle1)
    val y1 = ann.end.y + arrowSize * sin(angle1)
    val x2 = ann.end.x + arrowSize * cos(angle2)
    val y2 = ann.end.y + arrowSize * sin(angle2)

    drawLine(Color(ann.color.toULong()), Offset(ann.end.x, ann.end.y), Offset(x1, y1), ann.strokeWidth)
    drawLine(Color(ann.color.toULong()), Offset(ann.end.x, ann.end.y), Offset(x2, y2), ann.strokeWidth)
}

private fun DrawScope.drawShape(ann: AnnotationItem.Shape) {
    val left = min(ann.bounds.left, ann.bounds.right)
    val right = max(ann.bounds.left, ann.bounds.right)
    val top = min(ann.bounds.top, ann.bounds.bottom)
    val bottom = max(ann.bounds.top, ann.bounds.bottom)
    val rect = Rect(left, top, right, bottom)
    when (ann.shapeType) {
        ShapeType.RECTANGLE -> drawRect(
            color = Color(ann.color.toULong()),
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = ann.strokeWidth, cap = StrokeCap.Round)
        )
        ShapeType.CIRCLE -> {
            val radius = min(rect.width, rect.height) / 2f
            val center = rect.center
            drawCircle(
                color = Color(ann.color.toULong()),
                radius = radius,
                center = center,
                style = Stroke(width = ann.strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}
