// domain/usecase/project/GetProjectsUseCase.kt
package com.medprimetech.annotationapp.domain.usecase

import com.medprimetech.annotationapp.domain.model.Project
import com.medprimetech.annotationapp.domain.repository.ProjectRepository

class GetProjectsUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(): List<Project> {
        return repository.getProjects()
    }
}
