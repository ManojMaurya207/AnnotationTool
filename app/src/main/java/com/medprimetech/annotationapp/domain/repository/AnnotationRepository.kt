package com.medprimetech.annotationapp.domain.repository

import com.medprimetech.annotationapp.domain.model.AnnotationData


interface AnnotationRepository {
    suspend fun saveAnnotations(annotation: AnnotationData): Long
    suspend fun getAnnotations(projectId: Long): AnnotationData?
}
