package com.medprimetech.annotationapp.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.medprimetech.annotationapp.domain.model.AnnotationItem

@Entity(tableName = "annotations")
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long, // foreign key
    val annotations: List<AnnotationItem> // sealed class list
)
