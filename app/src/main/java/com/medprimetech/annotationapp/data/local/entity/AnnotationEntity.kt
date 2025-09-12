package com.medprimetech.annotationapp.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.medprimetech.annotationapp.domain.model.AnnotationItem

@Entity(
    tableName = "annotations",
    primaryKeys = ["id", "projectId"], // 👈 composite primary key
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class AnnotationEntity(
    val id: Long,            // no @PrimaryKey here
    val projectId: Long,
    val items: List<AnnotationItem> // stored as JSON
)