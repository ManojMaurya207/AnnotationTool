package com.medprimetech.annotationapp.data.local.dao// data/local/dao/ProjectDao.kt
import androidx.room.*
import com.medprimetech.annotationapp.data.local.entity.ProjectEntity

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity): Long

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Delete
    suspend fun delete(project: ProjectEntity)
}
