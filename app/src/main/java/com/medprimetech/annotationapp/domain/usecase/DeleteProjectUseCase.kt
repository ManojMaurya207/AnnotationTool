// domain/usecase/project/DeleteProjectUseCase.kt
package com.medprimetech.annotationapp.domain.usecase

import com.medprimetech.annotationapp.domain.model.Project
import com.medprimetech.annotationapp.domain.repository.ProjectRepository

class DeleteProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) {
        repository.deleteProject(project)
    }
}
