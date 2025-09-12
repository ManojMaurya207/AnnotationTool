package com.medprimetech.annotationapp.data.local.dao// data/local/dao/AnnotationDao.kt
import androidx.room.*
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnotationDao {
    @Query("SELECT * FROM annotations WHERE projectId = :projectId")
    fun getAnnotations(projectId: Long): Flow<List<AnnotationEntity>>

    // Get the latest annotations for saving/loading (one-shot)
    @Query("SELECT * FROM annotations WHERE projectId = :projectId LIMIT 1")
    suspend fun getAnnotationsOnce(projectId: Long): AnnotationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(annotation: AnnotationEntity): Long

    @Delete
    suspend fun delete(annotation: AnnotationEntity)
}