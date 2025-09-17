package com.medprimetech.annotationapp.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class AnnotationItem {
    @Serializable
    data class Freehand(val points: List<PointFSerializable>, val color: Long, val strokeWidth: Float) : AnnotationItem()

    @Serializable
    data class Shape(val shapeType: ShapeType, val bounds: RectSerializable, val color: Long, val strokeWidth: Float) : AnnotationItem()

    @Serializable
    data class Text(val text: String, val position: PointFSerializable, val color: Long, val fontSize: Float) : AnnotationItem()

    @Serializable
    data class Eraser(val path: List<PointFSerializable>, val strokeWidth: Float) : AnnotationItem()

    @Serializable
    data class Arrow(val start: PointFSerializable, val end: PointFSerializable, val color: Long, val strokeWidth: Float) : AnnotationItem()
}

@Serializable
enum class ShapeType {
    RECTANGLE,
    CIRCLE,
}
fun ShapeType.getIcon(): ImageVector{
    return when(this){
        ShapeType.RECTANGLE -> Icons.Outlined.Rectangle
        ShapeType.CIRCLE -> Icons.Outlined.Circle
    }
}
// Helper classes for serialization (Room can’t store Android PointF/RectF directly)
@Serializable
data class PointFSerializable(val x: Float, val y: Float)

@Serializable
data class RectSerializable(val left: Float, val top: Float, val right: Float, val bottom: Float)
