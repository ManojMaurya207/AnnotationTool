package com.medprimetech.annotationapp.data.local.dao// data/local/dao/ProjectDao.kt
import androidx.room.*
import com.medprimetech.annotationapp.data.local.entity.ProjectEntity
import com.medprimetech.annotationapp.domain.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity): Long

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: Long): Flow<ProjectEntity?>

    @Delete
    suspend fun delete(project: ProjectEntity)
}
