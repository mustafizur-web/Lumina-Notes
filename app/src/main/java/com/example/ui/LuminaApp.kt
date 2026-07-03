package com.example.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun LuminaApp() {
    val navController = rememberNavController()
    val viewModel: NoteViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    // Elegant sliding and fading animations for seamless screen transitions
    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted || isLoggedIn) "notes" else "onboarding",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStartedClick = {
                    viewModel.completeOnboarding()
                    navController.navigate("notes") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onLogInClick = {
                    viewModel.completeOnboarding()
                    navController.navigate("notes/profile") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    viewModel.completeOnboarding()
                    navController.navigate("notes") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("notes") {
            NotesListScreen(
                viewModel = viewModel,
                onNoteClick = { id, category ->
                    if (category.equals("Drawing", ignoreCase = true)) {
                        navController.navigate("draw/$id")
                    } else {
                        navController.navigate("edit/$id")
                    }
                },
                onProfileClick = {}
            )
        }

        composable("notes/profile") {
            NotesListScreen(
                viewModel = viewModel,
                onNoteClick = { id, category ->
                    if (category.equals("Drawing", ignoreCase = true)) {
                        navController.navigate("draw/$id")
                    } else {
                        navController.navigate("edit/$id")
                    }
                },
                onProfileClick = {},
                initialTab = "profile"
            )
        }

        composable(
            route = "edit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            EditNoteScreen(
                viewModel = viewModel,
                noteId = noteId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "draw/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            DrawNoteScreen(
                viewModel = viewModel,
                noteId = noteId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
