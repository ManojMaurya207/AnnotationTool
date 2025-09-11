package com.medprimetech.annotationapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medprimetech.annotationapp.domain.model.Project
import com.medprimetech.annotationapp.domain.usecase.project.ProjectUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val projectUseCases: ProjectUseCases
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    fun loadProjects() {
        viewModelScope.launch {
            _projects.value = projectUseCases.getProjects()
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            projectUseCases.addProject(project)
            loadProjects()
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectUseCases.deleteProject(project)
            loadProjects()
        }
    }
}