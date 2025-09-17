package com.medprimetech.annotationapp.presentation.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.PointFSerializable
import com.medprimetech.annotationapp.presentation.component.DrawingCanvas
import com.medprimetech.annotationapp.presentation.component.ToolBar
import com.medprimetech.annotationapp.presentation.model.DrawingAction
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showTextDialog by remember { mutableStateOf(false) }
    var tapPosition: PointFSerializable by remember { mutableStateOf(PointFSerializable(0f, 0f)) }
    val context = LocalContext.current

    //For flattening the image
    val baseBitmap = BitmapFactory.decodeFile(project?.imageUri)
//    val exportBitmap = baseBitmap.copy(Bitmap.Config.ARGB_8888, true)
//    val canvas = android.graphics.Canvas(exportBitmap)


    LaunchedEffect(Unit) {
        viewModel.onAction(DrawingAction.Load(projectId))
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.pendingTextPosition }
            .collect { pos ->
                if (pos != null) {
                    tapPosition = pos
                    showTextDialog = true
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annotate: ${project?.projectName ?: ""}") },
                actions = {
                    IconButton(onClick = {
                        viewModel.onAction(DrawingAction.Save(projectId))
                        Toast.makeText(context, "Annotation Saved", Toast.LENGTH_SHORT).show()
                    }) {
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
                        enabled = state.canUndo,
                        icon = Icons.Default.Undo,
                        label = "Undo",
                        onClick = { viewModel.onAction(DrawingAction.Undo) }
                    )
                    UndoRedoButton(
                        enabled = state.canRedo,
                        icon = Icons.Default.Redo,
                        label = "Redo",
                        onClick = { viewModel.onAction(DrawingAction.Redo) }
                    )
                }

                ToolBar(
                    state = state,
                    onAction = viewModel::onAction
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
                        viewModel = viewModel,
                        onAction = viewModel::onAction,
                        modifier = Modifier.size(500.dp),
                    )
                }
                if (showTextDialog) {
                    var input by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showTextDialog = false },
                        title = { Text("Enter text") },
                        text = {

                            OutlinedTextField(
                                value = input,
                                onValueChange = { input = it },
                                label = { Text("Annotation text") }
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                tapPosition.let { pos ->
                                    viewModel.onAction(
                                        DrawingAction.AddAnnotation(
                                            AnnotationItem.Text(
                                                text = input,
                                                position = pos,
                                                color = state.selectedColor.toColorLong(),
                                                fontSize = state.strokeWidth
                                            )
                                        )
                                    )
                                }
                                showTextDialog = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTextDialog = false }) {
                                Text("Cancel")
                            }
                        }
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
