// domain/usecase/annotation/SaveAnnotationsUseCase.kt
package com.medprimetech.annotationapp.domain.usecase

import com.medprimetech.annotationapp.domain.model.AnnotationData
import com.medprimetech.annotationapp.domain.repository.AnnotationRepository

class SaveAnnotationsUseCase(
    private val repository: AnnotationRepository
) {
    suspend operator fun invoke(annotation: AnnotationData): Long {
        return repository.saveAnnotations(annotation)
    }
}
