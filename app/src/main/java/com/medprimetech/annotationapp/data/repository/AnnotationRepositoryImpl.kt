package com.medprimetech.annotationapp.data.repository

import com.medprimetech.annotationapp.data.local.dao.AnnotationDao
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity
import com.medprimetech.annotationapp.domain.model.AnnotationData
import com.medprimetech.annotationapp.domain.repository.AnnotationRepository


class AnnotationRepositoryImpl(
    private val annotationDao: AnnotationDao
) : AnnotationRepository {

    override suspend fun saveAnnotations(annotation: AnnotationData): Long {
        return annotationDao.insert(
            AnnotationEntity(
                id = annotation.id,
                projectId = annotation.projectId,
                annotations = annotation.annotations
            )
        )
    }

    override suspend fun getAnnotations(projectId: Long): AnnotationData? {
        return annotationDao.getByProjectId(projectId)?.let {
            AnnotationData(it.id, it.projectId, it.annotations)
        }
    }
}
