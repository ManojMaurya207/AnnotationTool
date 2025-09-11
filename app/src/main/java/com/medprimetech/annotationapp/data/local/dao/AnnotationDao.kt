package com.medprimetech.annotationapp.data.local.dao// data/local/dao/AnnotationDao.kt
import androidx.room.*
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity

@Dao
interface AnnotationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(annotation: AnnotationEntity): Long

    @Query("SELECT * FROM annotations WHERE projectId = :projectId LIMIT 1")
    suspend fun getByProjectId(projectId: Long): AnnotationEntity?

    @Delete
    suspend fun delete(annotation: AnnotationEntity)
}
