// App.kt
package com.medprimetech.annotationapp.core.app

import android.app.Application
import com.medprimetech.annotationapp.core.app.di.appModule
import com.medprimetech.annotationapp.core.app.di.useCaseModule
import com.medprimetech.annotationapp.core.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule,viewModelModule,useCaseModule)
        }
    }
}
