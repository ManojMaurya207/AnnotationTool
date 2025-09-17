package com.medprimetech.annotationapp.presentation.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.PointFSerializable
import com.medprimetech.annotationapp.domain.model.ShapeType
import com.medprimetech.annotationapp.domain.model.ToolType

sealed class DrawingAction {
    data class AddAnnotation(val item: AnnotationItem) : DrawingAction()
    object Undo : DrawingAction()
    object Redo : DrawingAction()
    data class SelectTool(val tool: ToolType) : DrawingAction()
    data class SelectColor(val color: Color) : DrawingAction()
    data class SetStrokeWidth(val width: Float) : DrawingAction()
    data class SelectShape(val shape: ShapeType) : DrawingAction()
    object Clear : DrawingAction()
    data class Save(val projectId: Long) : DrawingAction()
    data class Load(val projectId: Long) : DrawingAction()
    data class RequestText(var position: PointFSerializable) : DrawingAction()
    data class Export(val projectId: Long) : DrawingAction()
}
