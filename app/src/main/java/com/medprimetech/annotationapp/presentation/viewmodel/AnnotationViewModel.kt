package com.medprimetech.annotationapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medprimetech.annotationapp.data.repository.ProjectRepositoryImpl
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import com.medprimetech.annotationapp.presentation.model.AnnotationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnnotationViewModel(
    private val projectRepository: ProjectRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = checkNotNull(savedStateHandle["projectId"]).toString().toLong()

    private val _uiState = MutableStateFlow(AnnotationUiState())
    val uiState: StateFlow<AnnotationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val project = projectRepository.getProjectById(projectId)
            _uiState.update { it.copy(imageUri = project?.imageUri) }
        }
    }

    fun addAnnotation(item: AnnotationItem) {
        _uiState.update { it.copy(annotations = it.annotations + item) }
    }
}