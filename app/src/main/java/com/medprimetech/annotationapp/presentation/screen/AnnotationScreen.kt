package com.medprimetech.annotationapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.medprimetech.annotationapp.presentation.component.DrawingCanvas
import com.medprimetech.annotationapp.presentation.component.ToolBar
import com.medprimetech.annotationapp.presentation.viewmodel.AnnotationViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationScreen(
    projectId: Long,
    viewModel: AnnotationViewModel = koinViewModel()
) {
    val project by viewModel.getProject(projectId).collectAsState(initial = null)
    val annotations by viewModel.annotations.collectAsState()

    val selectedTool = viewModel.selectedTool.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnnotations(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annotate: ${project?.projectName ?: ""}") },
                actions = {
                    IconButton(onClick = { viewModel.saveAnnotations(projectId) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    UndoRedoButton(
                        enabled = canUndo,
                        icon = Icons.Default.Undo,
                        label = "Undo",
                        onClick = { viewModel.undo() }
                    )
                    UndoRedoButton(
                        enabled = canRedo,
                        icon = Icons.Default.Redo,
                        label = "Redo",
                        onClick = { viewModel.redo() }
                    )
                }
                ToolBar(
                    selectedTool = selectedTool.value,
                    onToolSelected = { viewModel.selectTool(it) },
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            project?.let { proj ->
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Base image
                    AsyncImage(
                        model = File(proj.imageUri),
                        contentDescription = "Project Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(500.dp)
                    )

                    // Annotation overlay (same size as image)
                    DrawingCanvas(
                        annotations = annotations,
                        selectedTool = selectedTool.value,
                        onDraw = { item -> viewModel.addAnnotation(item) },
                        modifier = Modifier.size(500.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UndoRedoButton(
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(icon, contentDescription = label)
        }
        Text(text = label, fontSize = 12.sp)
    }
}
