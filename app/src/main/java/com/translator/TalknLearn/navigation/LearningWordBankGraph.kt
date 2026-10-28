package com.translator.TalknLearn

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.translator.TalknLearn.core.composableRequireLogin
import com.translator.TalknLearn.core.composableRequireLoginWithArgs
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.screens.learning.LearningScreen
import com.translator.TalknLearn.screens.learning.LearningSheetScreen
import com.translator.TalknLearn.screens.learning.LearningViewModel
import com.translator.TalknLearn.screens.learning.QuizScreen
import com.translator.TalknLearn.screens.wordbank.WordBankScreen
import com.translator.TalknLearn.screens.wordbank.WordBankViewModel
import com.translator.TalknLearn.screens.wordbank.CustomWordsViewModel

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
    customWordsViewModel: CustomWordsViewModel,
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

        if (primaryCode.isBlank() || targetCode.isBlank()) {
            navController.popBackStack()
            return@composableRequireLoginWithArgs
        }

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

        if (primaryCode.isBlank() || targetCode.isBlank()) {
            navController.popBackStack()
            return@composableRequireLoginWithArgs
        }

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
            customWordsViewModel = customWordsViewModel,
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }
}
