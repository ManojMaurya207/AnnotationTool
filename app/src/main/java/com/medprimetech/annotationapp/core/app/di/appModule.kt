// di/AppModule.kt
package com.medprimetech.annotationapp.core.app.di

import androidx.room.Room
import com.medprimetech.annotationapp.data.local.db.AppDatabase
import com.medprimetech.annotationapp.data.repository.AnnotationRepositoryImpl
import com.medprimetech.annotationapp.data.repository.ProjectRepositoryImpl
import com.medprimetech.annotationapp.domain.repository.AnnotationRepository
import com.medprimetech.annotationapp.domain.repository.ProjectRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {

    // --- Database ---
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "annotations_db"
        ).build()

    }

    // --- DAOs ---
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().annotationDao() }

    // --- Repositories ---
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<AnnotationRepository> { AnnotationRepositoryImpl(get()) }


}
