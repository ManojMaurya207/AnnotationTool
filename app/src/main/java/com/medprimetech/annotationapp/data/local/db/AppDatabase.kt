package com.medprimetech.annotationapp.data.local.db// data/local/db/AppDatabase.kt
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.medprimetech.annotationapp.data.local.dao.AnnotationDao
import com.medprimetech.annotationapp.data.local.dao.ProjectDao
import com.medprimetech.annotationapp.data.local.entity.ProjectEntity
import com.medprimetech.annotationapp.data.local.entity.AnnotationEntity

@Database(
    entities = [ProjectEntity::class, AnnotationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun annotationDao(): AnnotationDao
}