package com.medprimetech.annotationapp.core.app.di

import com.medprimetech.annotationapp.presentation.viewmodel.AnnotationViewModel
import com.medprimetech.annotationapp.presentation.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { AnnotationViewModel(get(),get()) }
}