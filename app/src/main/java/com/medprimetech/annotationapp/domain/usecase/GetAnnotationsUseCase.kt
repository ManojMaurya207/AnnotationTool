// domain/usecase/annotation/GetAnnotationsUseCase.kt
package com.medprimetech.annotationapp.domain.usecase

import com.medprimetech.annotationapp.domain.model.AnnotationData
import com.medprimetech.annotationapp.domain.repository.AnnotationRepository

class GetAnnotationsUseCase(
    private val repository: AnnotationRepository
) {
    suspend operator fun invoke(projectId: Long): AnnotationData? {
        return repository.getAnnotations(projectId)
    }
}
