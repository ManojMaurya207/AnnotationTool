package com.medprimetech.annotationapp.presentation.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.medprimetech.annotationapp.domain.model.ShapeType
import com.medprimetech.annotationapp.domain.model.ToolType
import androidx.compose.ui.graphics.Color
import com.medprimetech.annotationapp.domain.model.getIcon
import com.medprimetech.annotationapp.presentation.model.DrawingAction
import com.medprimetech.annotationapp.presentation.model.DrawingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBar(
    state: DrawingState,
    onAction: (DrawingAction) -> Unit
) {
    val context = LocalContext.current

    Column {

        // 🎨 Color palette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (color == state.selectedColor) 3.dp else 1.dp,
                            color = if (color == state.selectedColor) Color.Gray else Color.LightGray,
                            shape = CircleShape
                        )
                        .clickable {
                            onAction(DrawingAction.SelectColor(color))
                        }
                )
            }
        }

        // ✏️ Stroke width slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Width", modifier = Modifier.width(50.dp))
            Slider(
                value = state.strokeWidth,
                onValueChange = { onAction(DrawingAction.SetStrokeWidth(it)) },
                valueRange = 1f..20f,
                modifier = Modifier.weight(1f),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(state.selectedColor)
                    )
                },
                track ={
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Brush.linearGradient(listOf(Color.White,Color.Black)))
                    )

                }

            )
            Text(state.strokeWidth.toInt().toString())
        }

        //  Shape selector (only if Shape tool is active)
        if (state.selectedTool == ToolType.SHAPE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShapeType.entries.forEach { shape ->
                    Button(
                        onClick = { onAction(DrawingAction.SelectShape(shape)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.selectedShape == shape) Color.Gray else Color.LightGray
                        )
                    ) {
                        Icon(imageVector = shape.getIcon(), contentDescription = shape.name)
                    }
                }
            }
        }

        // 🛠 Tool selector row
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
                            selected = state.selectedTool == tool,
                            onClick = {
                                onAction(DrawingAction.SelectTool(tool))
                                Toast.makeText(
                                    context,
                                    "Selected Tool : ${tool.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            icon = { Icon(imageVector = icon, contentDescription = tool.name) },
                            label = { Text(tool.name) }
                        )
                    }
                }
            }
        }
    }
}
