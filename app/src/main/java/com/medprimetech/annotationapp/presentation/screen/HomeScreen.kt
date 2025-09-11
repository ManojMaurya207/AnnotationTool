// presentation/home/HomeScreen.kt
package com.medprimetech.annotationapp.presentation.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medprimetech.annotationapp.domain.model.Project
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.medprimetech.annotationapp.presentation.viewmodel.HomeViewModel
import java.io.File

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onProjectClick: (Long) -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    var newProjectName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument() // instead of GetContent
    ) { uri: Uri? ->
        uri?.let {
            // Persist permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedImageUri = uri
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(projects) { project ->
                Log.d("HomeScreen", "Project: ${project.imageUri}")
                ProjectItem(
                    project = project,
                    onClick = { onProjectClick(project.id) },
                    onLongPress = { projectToDelete = project }
                )
            }
        }
    }

    // Load projects on first composition
    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    // Dialog for creating new project
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false
                selectedImageUri = null
                showDialog = false
            },
            title = { Text("New Project") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = { Text("Project Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Show preview if selected
                    selectedImageUri?.let { uri ->
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(4.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                            ,
                            onError = {
                                selectedImageUri = null
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { launcher.launch(arrayOf("image/*"))  }) {
                        Text(
                            if (selectedImageUri == null) "Select Image"
                            else "Image Selected"
                        )
                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjectName.isNotBlank() && selectedImageUri != null) {
                            // Save to internal storage
                            val path = saveImageToInternalStorage(context, selectedImageUri!!)
                            viewModel.addProject(
                                Project(
                                    projectName = newProjectName,
                                    imageUri = path // stored as safe local path
                                )
                            )
                            newProjectName = ""
                            selectedImageUri = null
                            showDialog = false
                        }
                    },
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    selectedImageUri = null
                    showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },

        )
    }

    // Dialog for confirming project deletion
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Project") },
            text = { Text("Are you sure you want to delete '${projectToDelete?.projectName}'?") },
            confirmButton = {
                Button(onClick = {
                    projectToDelete?.let { viewModel.deleteProject(it) }
                    projectToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProjectItem(
    project: Project,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val density = LocalDensity.current
            val radiusPx = with(density) { 10.dp.toPx() }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(project.imageUri.toUri())
                    .transformations(RoundedCornersTransformation(radiusPx))
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                text = project.projectName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}


fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
    val file = File(context.filesDir, "project_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file.absolutePath // now you have a real file path
}