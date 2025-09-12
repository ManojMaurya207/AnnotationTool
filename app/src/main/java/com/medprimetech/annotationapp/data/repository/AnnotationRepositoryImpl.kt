package com.medprimetech.annotationapp.data.repository

import com.medprimetech.annotationapp.data.local.dao.AnnotationDao
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity
import com.medprimetech.annotationapp.domain.model.AnnotationData
import com.medprimetech.annotationapp.domain.repository.AnnotationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AnnotationRepositoryImpl(
    private val annotationDao: AnnotationDao
) : AnnotationRepository {

    override suspend fun saveAnnotations(annotation: AnnotationData): Long {
        return annotationDao.insert(
            AnnotationEntity(
                id = annotation.id,
                projectId = annotation.projectId,
                items = annotation.annotations
            )
        )
    }

    // For one-shot load (e.g., restoring state in ViewModel)
    override suspend fun getAnnotations(projectId: Long): AnnotationData? {
        return annotationDao.getAnnotationsOnce(projectId)?.let { entity ->
            AnnotationData(
                id = entity.id,
                projectId = entity.projectId,
                annotations = entity.items
            )
        }
    }

    // For continuous UI observation
    fun observeAnnotations(projectId: Long): Flow<List<AnnotationData>> {
        return annotationDao.getAnnotations(projectId).map { entities ->
            entities.map { entity ->
                AnnotationData(
                    id = entity.id,
                    projectId = entity.projectId,
                    annotations = entity.items
                )
            }
        }
    }
}
