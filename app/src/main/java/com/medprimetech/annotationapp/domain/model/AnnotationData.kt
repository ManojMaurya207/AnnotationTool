// domain/model/AnnotationData.kt
package com.medprimetech.annotationapp.domain.model

data class AnnotationData(
    val id: Long = 0,
    val projectId: Long,
    val annotations: List<AnnotationItem>
)
