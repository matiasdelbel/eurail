package com.eurail.app.ui.screens.list

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.buildkt.material3.tokens.spacers
import com.eurail.app.R
import com.eurail.app.domain.Article
import com.eurail.app.ui.components.ArticleCard
import com.eurail.app.ui.components.ArticleListShimmer
import com.eurail.app.ui.components.EmptyState
import com.eurail.app.ui.components.ErrorScreen
import com.eurail.app.ui.theme.EurailTheme
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    onArticleClick: (Article) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    uiState: ArticleListUiState
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.help_articles)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(
                targetState = uiState.isLoading && uiState.articles.isEmpty(),
                label = "content_crossfade"
            ) { isInitialLoading ->
                when {
                    isInitialLoading -> ArticleListShimmer()

                    uiState.error != null && uiState.articles.isEmpty() -> ErrorScreen(
                        error = uiState.error,
                        onRetry = onRetry
                    )

                    else -> ArticleListContent(
                        uiState = uiState,
                        onArticleClick = onArticleClick,
                        onCategorySelected = onCategorySelected,
                        onRefresh = onRefresh
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleListContent(
    uiState: ArticleListUiState,
    onArticleClick: (Article) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        FilterBar(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isEmpty && !uiState.isLoading) {
                EmptyState(
                    title = when {
                        uiState.selectedCategory != null -> stringResource(R.string.no_results)
                        else -> stringResource(R.string.no_articles)
                    },
                    message = when {
                        uiState.selectedCategory != null -> stringResource(R.string.try_adjusting_your_search_or_filters)
                        else -> stringResource(R.string.no_help_articles_available_yet)
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(MaterialTheme.spacers.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacers.small)
                ) {
                    items(
                        items = uiState.filteredArticles,
                        key = { it.id }
                    ) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterBar(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    if (categories.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacers.small)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text(text = stringResource(id = R.string.all)) },
                    modifier = Modifier.padding(start = MaterialTheme.spacers.medium)
                )
            }
            itemsIndexed(items = categories) { index, category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                    label = { Text(text = category) },
                    modifier = if (index == categories.lastIndex) Modifier.padding(end = MaterialTheme.spacers.medium) else Modifier
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleListScreen_NoArticles() {
    EurailTheme {
        ArticleListScreen(
            uiState = ArticleListUiState(),
            onArticleClick = {},
            onCategorySelected = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleListScreen_ShowArticles() {
    EurailTheme {
        ArticleListScreen(
            uiState = ArticleListUiState(
                articles = listOf(
                    Article(
                        id = "1",
                        title = "Getting Started with Your Rail Pass",
                        summary = "Learn how to activate and use your Eurail pass for the first time.",
                        content = "",
                        updatedAt = LocalDateTime.parse(input = "2024-03-10T14:30:00Z"),
                        category = "Getting Started",
                    )
                ),
                filteredArticles = listOf(
                    Article(
                        id = "1",
                        title = "Getting Started with Your Rail Pass",
                        summary = "Learn how to activate and use your Eurail pass for the first time.",
                        content = "",
                        updatedAt = LocalDateTime.parse(input = "2024-03-10T14:30:00Z"),
                        category = "Getting Started",
                    )
                )
            ),
            onArticleClick = {},
            onCategorySelected = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleListScreen_ShowCachedArticles() {
    EurailTheme {
        ArticleListScreen(
            uiState = ArticleListUiState(
                articles = listOf(
                    Article(
                        id = "1",
                        title = "Getting Started with Your Rail Pass",
                        summary = "Learn how to activate and use your Eurail pass for the first time.",
                        content = "",
                        updatedAt = LocalDateTime.parse(input = "2024-03-10T14:30:00Z"),
                        category = "Getting Started",
                    )
                ),
                filteredArticles = listOf(
                    Article(
                        id = "1",
                        title = "Getting Started with Your Rail Pass",
                        summary = "Learn how to activate and use your Eurail pass for the first time.",
                        content = "",
                        updatedAt = LocalDateTime.parse(input = "2024-03-10T14:30:00Z"),
                        category = "Getting Started",
                    )
                )
            ),
            onArticleClick = {},
            onCategorySelected = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}