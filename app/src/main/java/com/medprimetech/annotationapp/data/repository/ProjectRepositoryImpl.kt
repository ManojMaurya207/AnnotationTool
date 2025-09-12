package com.medprimetech.annotationapp.data.repository

import com.medprimetech.annotationapp.data.local.dao.ProjectDao
import com.medprimetech.annotationapp.data.local.entity.ProjectEntity
import com.medprimetech.annotationapp.domain.model.Project
import com.medprimetech.annotationapp.domain.repository.ProjectRepository


class ProjectRepositoryImpl(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override suspend fun addProject(project: Project): Long {
        return projectDao.insert(
            ProjectEntity(
                id = project.id,
                projectName = project.projectName,
                imageUri = project.imageUri
            )
        )
    }

    override suspend fun getProjects(): List<Project> {
        return projectDao.getAllProjects().map {
            Project(it.id, it.projectName, it.imageUri)
        }
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.delete(
            ProjectEntity(
                id = project.id,
                projectName = project.projectName,
                imageUri = project.imageUri
            )
        )
    }
}
