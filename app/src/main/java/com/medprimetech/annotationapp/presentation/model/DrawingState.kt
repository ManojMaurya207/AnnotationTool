package com.medprimetech.annotationapp.presentation.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.PointFSerializable
import com.medprimetech.annotationapp.domain.model.ShapeType
import com.medprimetech.annotationapp.domain.model.ToolType

data class DrawingState(
    val annotations: List<AnnotationItem> = emptyList(),
    val pendingTextPosition: PointFSerializable? = null,
    val canUndo: Boolean = true,
    val canRedo: Boolean = false,
    val selectedTool: ToolType = ToolType.FREEHAND,
    val selectedColor: Color = Color.Black,
    val strokeWidth: Float = 10f,
    val selectedShape: ShapeType = ShapeType.RECTANGLE
)
