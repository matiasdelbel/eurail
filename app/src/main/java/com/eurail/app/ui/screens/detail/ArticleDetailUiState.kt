package com.eurail.app.ui.screens.detail

sealed interface ArticleDetailUiState{

    data class Error(
        val error: com.eurail.app.domain.Error
    ): ArticleDetailUiState

    data object Loading: ArticleDetailUiState

    data class Success(
        val title: String,
        val summary: String,
        val content: String,
        val updatedAt: String,
        val category: String,
        val isRefreshing: Boolean = false,
    ): ArticleDetailUiState
}
