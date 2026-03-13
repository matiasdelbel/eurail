package com.eurail.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eurail.app.data.ArticleRepository
import com.eurail.app.ui.screens.detail.ArticleDetailScreen
import com.eurail.app.ui.screens.detail.ArticleDetailViewModel
import com.eurail.app.ui.screens.list.ArticleListScreen
import com.eurail.app.ui.screens.list.ArticleListViewModel

sealed class Screen(val route: String) {

    data object ArticleList : Screen("articles")

    data object ArticleDetail : Screen("articles/{articleId}") {

        operator fun invoke(articleId: String) = "articles/$articleId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    repository: ArticleRepository,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ArticleList.route
    ) {
        composable(route = Screen.ArticleList.route) {
            val viewModel = remember { ArticleListViewModel(repository) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ArticleListScreen(
                uiState = uiState,
                onCategorySelected = viewModel::onCategorySelected,
                onRetry = viewModel::retry,
                onRefresh = viewModel::refreshArticles,
                onArticleClick = { article -> navController.navigate(route = Screen.ArticleDetail(articleId = article.id)) }
            )
        }

        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument(name = "articleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: return@composable
            val viewModel = remember(articleId) { ArticleDetailViewModel(articleId, repository) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ArticleDetailScreen(
                uiState = uiState,
                onRetry = viewModel::retry,
                onRefresh = viewModel::refresh,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
