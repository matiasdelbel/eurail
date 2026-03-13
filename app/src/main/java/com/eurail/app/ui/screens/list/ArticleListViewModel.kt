package com.eurail.app.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eurail.app.data.ArticleRepository
import com.eurail.app.domain.Result
import com.eurail.app.domain.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val repository: ArticleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleListUiState())
    val uiState: StateFlow<ArticleListUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    fun refreshArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            when (val result = repository.refreshArticles()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            articles = result.data,
                            filteredArticles = filterArticles(
                                articles = result.data,
                                category = state.selectedCategory
                            ),
                            isRefreshing = false,
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isRefreshing = false,
                            error = if (state.articles.isEmpty()) result.error else null
                        )
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredArticles = filterArticles(articles = state.articles, category)
            )
        }
    }

    fun retry() {
        loadArticles(forceRefresh = true)
    }

    private fun loadArticles(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getArticles(forceRefresh)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            articles = result.data,
                            filteredArticles = filterArticles(
                                articles = result.data,
                                category = state.selectedCategory
                            ),
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    private fun filterArticles(articles: List<Article>, category: String?): List<Article> =
        articles.filter { article -> category == null || article.category == category }
}
