package com.medprimetech.annotationapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medprimetech.annotationapp.data.local.dao.AnnotationDao
import com.medprimetech.annotationapp.data.local.dao.ProjectDao
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity
import com.medprimetech.annotationapp.data.local.entity.ProjectEntity
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.domain.model.ToolType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnotationViewModel(
    private val annotationDao: AnnotationDao,
    private val projectDao: ProjectDao
) : ViewModel() {
        private val undoStack = ArrayDeque<AnnotationItem>() // visible annotations
        private val redoStack = ArrayDeque<AnnotationItem>() // undone annotations

        private val _annotations = MutableStateFlow<List<AnnotationItem>>(emptyList())
        val annotations: StateFlow<List<AnnotationItem>> = _annotations.asStateFlow()

        private val _canUndo = MutableStateFlow(false)
        val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

        private val _canRedo = MutableStateFlow(false)
        val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

        private val _selectedTool = MutableStateFlow(ToolType.FREEHAND)
        val selectedTool: StateFlow<ToolType> = _selectedTool.asStateFlow()

        /** Expose project */
        fun getProject(projectId: Long): Flow<ProjectEntity?> =
            projectDao.getProjectById(projectId)


         /** Load existing annotations for this project */
        fun loadAnnotations(projectId: Long) {
            viewModelScope.launch {
                annotationDao.getAnnotations(projectId).collect { entities ->
                    val items = entities.firstOrNull()?.items ?: emptyList()
                    loadFromDb(items)
                }
            }
        }

        /** Add new annotation */
        fun addAnnotation(item: AnnotationItem) {
            undoStack.addLast(item)
            redoStack.clear() // new action clears redo
            _annotations.value = undoStack.toList()
            updateUndoRedoState()
        }

        fun undo() {
            if (undoStack.isNotEmpty()) {
                val removed = undoStack.removeLast()
                redoStack.addLast(removed)
                _annotations.value = undoStack.toList()
                updateUndoRedoState()
            }
        }

        fun redo() {
            if (redoStack.isNotEmpty()) {
                val restored = redoStack.removeLast()
                undoStack.addLast(restored)
                _annotations.value = undoStack.toList()
                updateUndoRedoState()
            }
        }

        private fun loadFromDb(items: List<AnnotationItem>) {
            undoStack.clear()
            redoStack.clear()
            undoStack.addAll(items)
            _annotations.value = undoStack.toList()
            updateUndoRedoState()
        }

        fun saveAnnotations(projectId: Long) {
            viewModelScope.launch {
                val entity = AnnotationEntity(
                    id = projectId,
                    projectId = projectId,
                    items = _annotations.value
                )
                annotationDao.insert(entity)
            }
        }

        fun clearAnnotations() {
            undoStack.clear()
            redoStack.clear()
            _annotations.value = emptyList()
            updateUndoRedoState()
        }

        fun selectTool(tool: ToolType) {
            _selectedTool.value = tool
        }

        private fun updateUndoRedoState() {
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = redoStack.isNotEmpty()
        }
    }
