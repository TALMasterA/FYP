package com.example.fyp.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Helper extension that wraps a composable with RequireLoginGate.
 * Reduces boilerplate for login-protected routes.
 *
 * Usage:
 * ```
 * composableRequireLogin(
 *     route = AppScreen.History.route,
 *     onNeedLogin = { navController.navigate(AppScreen.Login.route) { launchSingleTop = true } }
 * ) {
 *     HistoryScreen(onBack = { navController.popBackStack() })
 * }
 * ```
 */
fun NavGraphBuilder.composableRequireLogin(
    route: String,
    onNeedLogin: () -> Unit,
    arguments: List<androidx.navigation.NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route = route, arguments = arguments) { backStackEntry ->
        RequireLoginGate(
            content = { content(backStackEntry) },
            onNeedLogin = onNeedLogin
        )
    }
}

/**
 * Helper extension for login-gated routes with path arguments.
 * Automatically creates NavArgument list from argument names.
 *
 * Usage:
 * ```
 * composableRequireLoginWithArgs(
 *     route = "learning_sheet/{primaryCode}/{targetCode}",
 *     argNames = listOf("primaryCode", "targetCode"),
 *     onNeedLogin = { navController.navigate(AppScreen.Login.route) { launchSingleTop = true } }
 * ) { backStackEntry ->
 *     val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
 *     val targetCode = backStackEntry.arguments?.getString("targetCode").orEmpty()
 *     LearningSheetScreen(primaryCode, targetCode, ...)
 * }
 * ```
 */
fun NavGraphBuilder.composableRequireLoginWithArgs(
    route: String,
    argNames: List<String>,
    onNeedLogin: () -> Unit,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    val arguments = argNames.map { name ->
        navArgument(name) { type = NavType.StringType }
    }
    composableRequireLogin(
        route = route,
        arguments = arguments,
        onNeedLogin = onNeedLogin,
        content = content
    )
}
