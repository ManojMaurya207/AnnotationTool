package com.medprimetech.annotationapp.domain.usecase.annotation

import com.medprimetech.annotationapp.domain.usecase.GetAnnotationsUseCase
import com.medprimetech.annotationapp.domain.usecase.SaveAnnotationsUseCase

data class AnnotationUseCases(
    val saveAnnotations: SaveAnnotationsUseCase,
    val getAnnotations: GetAnnotationsUseCase
)