package com.medprimetech.annotationapp.data.local.db// data/local/db/Converters.kt
import androidx.room.TypeConverter
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromAnnotationList(list: List<AnnotationItem>?): String {
        return list?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toAnnotationList(jsonString: String): List<AnnotationItem> {
        return json.decodeFromString(jsonString)
    }
}