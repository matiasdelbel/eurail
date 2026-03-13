package com.eurail.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eurail.app.data.ArticleRepository
import com.eurail.app.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class ArticleDetailViewModel(
    private val articleId: String,
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(value = ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    fun refresh() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ArticleDetailUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }
            loadArticle(forceRefresh = true)
        }
    }

    fun retry() {
        loadArticle(forceRefresh = true)
    }

    private fun loadArticle(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { ArticleDetailUiState.Loading }

            when (val result = repository.getArticleDetail(articleId, forceRefresh)) {
                is Result.Success -> _uiState.update {
                    ArticleDetailUiState.Success(
                        title = result.data.title,
                        summary = result.data.summary,
                        content = result.data.content,
                        updatedAt = result.data.updatedAt.formatedString(),
                        category = result.data.category,
                    )
                }
                is Result.Error -> _uiState.update {
                    ArticleDetailUiState.Error(error = result.error)
                }
            }
        }
    }

    private fun LocalDateTime.formatedString(): String {
        return try {
            val month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val day = dayOfMonth
            val year = year
            "$month $day, $year"
        } catch (_: Exception) {
            toString()
        }
    }
}
