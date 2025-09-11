package com.medprimetech.annotationapp.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.medprimetech.annotationapp.domain.model.AnnotationItem

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    annotations: List<AnnotationItem>,
    onDrawPath: (AnnotationItem) -> Unit
) {
    Canvas(modifier = modifier) {
        annotations.forEach { annotation ->
            drawPath(
                path = annotation.path,
                color = annotation.color,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}