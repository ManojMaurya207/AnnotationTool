package com.medprimetech.annotationapp.presentation.component

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.medprimetech.annotationapp.domain.model.ToolType
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import java.nio.file.WatchEvent

@Composable
fun ToolBar(
    selectedTool: ToolType,
    onToolSelected: (ToolType) -> Unit,
) {
    val context = LocalContext.current

    NavigationBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                ToolType.entries.forEach { tool ->
                    val icon = when (tool) {
                        ToolType.FREEHAND -> Icons.Default.Brush
                        ToolType.SHAPE -> Icons.Default.Create
                        ToolType.TEXT -> Icons.Default.TextFields
                        ToolType.ARROW -> Icons.Default.ArrowOutward
                        ToolType.ERASER -> Icons.Default.HighlightOff
                    }

                    NavigationBarItem(
                        selected = selectedTool == tool,
                        onClick = {
                            onToolSelected(tool)
                            Toast.makeText(context, "Selected Tool : ${tool.name}", Toast.LENGTH_SHORT).show()
                        },
                        icon = { Icon(imageVector = icon, contentDescription = tool.name) },
                        label = { Text(tool.name) }
                    )
                }
            }


        }
    }
}
