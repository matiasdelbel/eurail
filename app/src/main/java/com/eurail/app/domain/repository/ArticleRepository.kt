package com.eurail.app.domain.repository

import com.eurail.app.domain.Article
import com.eurail.app.domain.Result

interface ArticleRepository {

    suspend fun getArticles(forceRefresh: Boolean = false): Result<List<Article>>

    suspend fun getArticleDetail(id: String, forceRefresh: Boolean = false): Result<Article>

    suspend fun refreshArticles(): Result<List<Article>>
}
