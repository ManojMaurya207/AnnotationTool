// di/UseCaseModule.kt
package com.medprimetech.annotationapp.core.app.di

import com.medprimetech.annotationapp.domain.usecase.*
import com.medprimetech.annotationapp.domain.usecase.project.*
import com.medprimetech.annotationapp.domain.usecase.annotation.*
import org.koin.dsl.module

val useCaseModule = module {

    // Project use cases
    factory { AddProjectUseCase(get()) }
    factory { GetProjectsUseCase(get()) }
    factory { DeleteProjectUseCase(get()) }
    single {
        ProjectUseCases(
            addProject = get(),
            getProjects = get(),
            deleteProject = get()
        )
    }

    // Annotation use cases
    factory { SaveAnnotationsUseCase(get()) }
    factory { GetAnnotationsUseCase(get()) }
    single {
        AnnotationUseCases(
            saveAnnotations = get(),
            getAnnotations = get()
        )
    }
}
