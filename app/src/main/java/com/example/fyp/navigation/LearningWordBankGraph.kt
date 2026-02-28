package com.example.fyp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.core.composableRequireLoginWithArgs
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.screens.learning.LearningScreen
import com.example.fyp.screens.learning.LearningSheetScreen
import com.example.fyp.screens.learning.LearningViewModel
import com.example.fyp.screens.learning.QuizScreen
import com.example.fyp.screens.wordbank.WordBankScreen
import com.example.fyp.screens.wordbank.WordBankViewModel

/**
 * Navigation sub-graph: Learning sheets, quizzes, and word bank.
 */
internal fun NavGraphBuilder.learningWordBankGraph(
    navController: NavController,
    navigateToLogin: () -> Unit,
    appLanguageState: AppLanguageState,
    updateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiLanguages: List<Pair<String, String>>,
    learningViewModel: LearningViewModel,
    wordBankViewModel: WordBankViewModel,
    primaryLanguageCode: String,
) {
    composableRequireLogin(
        route = AppScreen.Learning.route,
        onNeedLogin = navigateToLogin
    ) {
        LearningScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = {
                if (!navController.navigateUp()) {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            viewModel = learningViewModel,
            onOpenSheet = { primary, target ->
                navController.navigate(AppScreen.LearningSheet.routeFor(primary, target)) {
                    launchSingleTop = true
                }
            },
            onOpenWordBank = {
                navController.navigate(AppScreen.WordBank.route) { launchSingleTop = true }
            }
        )
    }

    composableRequireLoginWithArgs(
        route = AppScreen.LearningSheet.route,
        argNames = listOf("primaryCode", "targetCode"),
        onNeedLogin = navigateToLogin
    ) { backStackEntry ->
        val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
        val targetCode  = backStackEntry.arguments?.getString("targetCode").orEmpty()

        LearningSheetScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            primaryCode = primaryCode,
            targetCode = targetCode,
            onBack = { navController.popBackStack() },
            learningViewModel = learningViewModel,
            onOpenQuiz = {
                navController.navigate(AppScreen.Quiz.routeFor(primaryCode, targetCode)) {
                    launchSingleTop = true
                }
            }
        )
    }

    composableRequireLoginWithArgs(
        route = AppScreen.Quiz.route,
        argNames = listOf("primaryCode", "targetCode"),
        onNeedLogin = navigateToLogin
    ) { backStackEntry ->
        val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
        val targetCode  = backStackEntry.arguments?.getString("targetCode").orEmpty()

        QuizScreen(
            appLanguageState = appLanguageState,
            primaryCode = primaryCode,
            targetCode = targetCode,
            onBack = { navController.popBackStack() },
            learningViewModel = learningViewModel
        )
    }

    composableRequireLogin(
        route = AppScreen.WordBank.route,
        onNeedLogin = navigateToLogin
    ) {
        WordBankScreen(
            viewModel = wordBankViewModel,
            appLanguageState = appLanguageState,
            primaryLanguageCode = primaryLanguageCode,
            onBack = { navController.popBackStack() }
        )
    }
}
