package com.eurail.app.ui.screens.list

import com.eurail.app.domain.Error
import com.eurail.app.domain.Article

data class ArticleListUiState(
    val articles: List<Article> = emptyList(),
    val filteredArticles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Error? = null,
    val selectedCategory: String? = null,
    val isOffline: Boolean = false,
) {
    val isEmpty: Boolean
        get() = filteredArticles.isEmpty() && !isLoading

    val categories: List<String>
        get() = articles.map { it.category }.distinct().sorted()
}
