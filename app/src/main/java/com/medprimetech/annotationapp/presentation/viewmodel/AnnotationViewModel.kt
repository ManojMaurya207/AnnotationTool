package com.medprimetech.annotationapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medprimetech.annotationapp.data.local.dao.AnnotationDao
import com.medprimetech.annotationapp.data.local.dao.ProjectDao
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.presentation.model.DrawingAction
import com.medprimetech.annotationapp.presentation.model.DrawingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnotationViewModel(
    private val annotationDao: AnnotationDao,
    private val projectDao: ProjectDao
) : ViewModel() {

    private val _state = MutableStateFlow(DrawingState())
    val state: StateFlow<DrawingState> = _state.asStateFlow()

    private val undoStack = ArrayDeque<AnnotationItem>() // history of drawn items
    private val redoStack = ArrayDeque<AnnotationItem>() // history of undone items

    fun onAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.AddAnnotation -> {
                undoStack.addLast(action.item)
                redoStack.clear()
                updateState {
                    it.copy(
                        annotations = undoStack.toList(),
                        pendingTextPosition = null // reset after use
                    )
                }
            }

            DrawingAction.Clear -> {
                // push all current items into redo stack (so Clear can be undone if needed)
                redoStack.addAll(undoStack)
                undoStack.clear()
                updateState { it.copy(annotations = emptyList()) }
            }

            DrawingAction.Undo -> {
                if (undoStack.isNotEmpty()) {
                    val removed = undoStack.removeLast()
                    redoStack.addLast(removed)
                    updateState { it.copy(annotations = undoStack.toList()) }
                }
            }

            DrawingAction.Redo -> {
                if (redoStack.isNotEmpty()) {
                    val restored = redoStack.removeLast()
                    undoStack.addLast(restored)
                    updateState { it.copy(annotations = undoStack.toList()) }
                }
            }

            is DrawingAction.SelectTool ->
                updateState { it.copy(selectedTool = action.tool) }

            is DrawingAction.SelectColor ->
                updateState { it.copy(selectedColor = action.color) }

            is DrawingAction.SetStrokeWidth ->
                updateState { it.copy(strokeWidth = action.width) }

            is DrawingAction.SelectShape ->
                updateState { it.copy(selectedShape = action.shape) }

            is DrawingAction.Save -> {
                viewModelScope.launch {
                    val entity = AnnotationEntity(
                        id = action.projectId,
                        projectId = action.projectId,
                        items = _state.value.annotations
                    )
                    annotationDao.insert(entity)
                }
            }

            is DrawingAction.Load -> {
                viewModelScope.launch {
                    annotationDao.getAnnotations(action.projectId).collect { entities ->
                        val items = entities.firstOrNull()?.items ?: emptyList()
                        undoStack.clear()
                        redoStack.clear()
                        undoStack.addAll(items)
                        updateState { it.copy(annotations = undoStack.toList()) }
                        refreshUndoRedoFlags()
                    }
                }
            }

            is DrawingAction.RequestText -> {
                updateState { it.copy(pendingTextPosition = action.position) }
            }

            is DrawingAction.Export -> {

            }
        }
        refreshUndoRedoFlags()
    }

    private fun updateState(reducer: (DrawingState) -> DrawingState) {
        _state.value = reducer(_state.value)
    }

    private fun refreshUndoRedoFlags() {
        updateState {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun getProject(projectId: Long) = projectDao.getProjectById(projectId)
}
