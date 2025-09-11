package com.medprimetech.annotationapp.domain.usecase.project

import com.medprimetech.annotationapp.domain.usecase.AddProjectUseCase
import com.medprimetech.annotationapp.domain.usecase.DeleteProjectUseCase
import com.medprimetech.annotationapp.domain.usecase.GetProjectsUseCase

data class ProjectUseCases(
    val addProject: AddProjectUseCase,
    val getProjects: GetProjectsUseCase,
    val deleteProject: DeleteProjectUseCase
)