package com.medprimetech.annotationapp.data.local.db

import androidx.room.TypeConverter
import com.medprimetech.annotationapp.domain.model.AnnotationItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AnnotationConverters {

    private val json = Json {
        ignoreUnknownKeys = true  // avoids crashes if model evolves
        encodeDefaults = true
    }

    @TypeConverter
    fun fromAnnotationList(items: List<AnnotationItem>?): String? {
        return items?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toAnnotationList(data: String?): List<AnnotationItem>? {
        return data?.let { json.decodeFromString(it) }
    }
}