package com.medprimetech.annotationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.medprimetech.annotationapp.presentation.navigation.AppNavHost
import com.medprimetech.annotationapp.core.app.theme.AnnotationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnnotationAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}