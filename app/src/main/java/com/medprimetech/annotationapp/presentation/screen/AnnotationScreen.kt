package com.medprimetech.annotationapp.presentation.screen

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.presentation.viewmodel.AnnotationViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun AnnotationScreen(
    projectId: Long,
    projectImagePath: String,
    onBack: () -> Unit,
    viewModel: AnnotationViewModel = koinViewModel() // inject with params later
) {
    val annotations by viewModel.annotations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annotate") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar = {
            ToolBar(
                currentTool = viewModel.currentTool,
                onToolSelected = { viewModel.setTool(it) },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Base project image
            AsyncImage(
                model = File(projectImagePath),
                contentDescription = "Project Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            )

            // Canvas overlay
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(viewModel.currentTool) {
                        detectDragGestures { change, _ ->
                            val position = change.position
                            if (viewModel.currentTool == ToolType.Freehand) {
                                viewModel.addAnnotation(
                                    AnnotationItem.Freehand(
                                        points = listOf(Offset(position.x, position.y)),
                                        color = Color.Red.value.toInt(),
                                        strokeWidth = 4f
                                    )
                                )
                            }
                        }
                    }
            ) {
                // Draw all annotations
                annotations.forEach { ann ->
                    when (ann) {
                        is AnnotationItem.Freehand -> {
                            for (i in 1 until ann.points.size) {
                                drawLine(
                                    color = Color(ann.color),
                                    start = ann.points[i - 1],
                                    end = ann.points[i],
                                    strokeWidth = ann.strokeWidth
                                )
                            }
                        }
                        is AnnotationItem.Arrow -> {
                            drawLine(
                                color = Color(ann.color),
                                start = ann.start,
                                end = ann.end,
                                strokeWidth = ann.strokeWidth
                            )
                        }
                        is AnnotationItem.Text -> {
                            drawContext.canvas.nativeCanvas.drawText(
                                ann.text,
                                ann.position.x,
                                ann.position.y,
                                Paint().apply {
                                    color = ann.color
                                    textSize = 42f
                                }
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
