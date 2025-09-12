// presentation/navigation/AppNavHost.kt
package com.medprimetech.annotationapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(
            route = Routes.ANNOTATION,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            AnnotationScreen(projectId = projectId)
        }

    }
}

object Routes {
    const val HOME = "home"
    const val ANNOTATION = "annotation/{projectId}"
}