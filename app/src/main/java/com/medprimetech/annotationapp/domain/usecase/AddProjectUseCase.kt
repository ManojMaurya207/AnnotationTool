// domain/usecase/project/AddProjectUseCase.kt
package com.medprimetech.annotationapp.domain.usecase

import com.medprimetech.annotationapp.domain.model.Project
import com.medprimetech.annotationapp.domain.repository.ProjectRepository

class AddProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(project: Project): Long {
        return repository.addProject(project)
    }
}
