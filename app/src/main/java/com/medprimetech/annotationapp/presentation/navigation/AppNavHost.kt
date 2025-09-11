// presentation/navigation/AppNavHost.kt
package com.medprimetech.annotationapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medprimetech.annotationapp.presentation.screen.AnnotationScreen
import com.medprimetech.annotationapp.presentation.screen.HomeScreen


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(onProjectClick = { projectId ->
                navController.navigate("annotation/$projectId")
            })
        }
        composable(Routes.ANNOTATION) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLong() ?: 0L
            AnnotationScreen(projectId = projectId)
        }
    }
}

object Routes {
    const val HOME = "home"
    const val ANNOTATION = "annotation/{projectId}"
}