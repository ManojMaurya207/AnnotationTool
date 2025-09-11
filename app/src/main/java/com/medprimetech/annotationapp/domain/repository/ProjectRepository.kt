package com.medprimetech.annotationapp.domain.repository

import com.medprimetech.annotationapp.domain.model.Project

interface ProjectRepository {
    suspend fun addProject(project: Project): Long
    suspend fun getProjects(): List<Project>
    suspend fun deleteProject(project: Project)

}
