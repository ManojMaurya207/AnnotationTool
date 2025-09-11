package com.medprimetech.annotationapp.presentation.model

import com.medprimetech.annotationapp.domain.model.AnnotationItem

data class AnnotationUiState(
    val imageUri: String? = null,
    val annotations: List<AnnotationItem> = emptyList()
)