package com.eurail.app.data

import com.eurail.app.data.remote.ApiResult
import com.eurail.app.data.remote.ArticleApiService
import com.eurail.app.data.remote.dto.ArticleDetailDto
import com.eurail.app.data.remote.dto.ArticleDto
import com.eurail.app.domain.Article
import com.eurail.app.domain.Result
import com.eurail.app.domain.Source
import com.eurail.shared.cache.ArticleCache
import com.eurail.shared.cache.CacheAge
import com.eurail.shared.cache.CachedArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface ArticleRepository {

    suspend fun getArticles(forceRefresh: Boolean = false): Result<List<Article>>

    suspend fun getArticleDetail(id: String, forceRefresh: Boolean = false): Result<Article>

    suspend fun refreshArticles(): Result<List<Article>>

    fun observeArticles(): Flow<List<Article>?>
}

class ArticleRepositoryImpl(
    private val apiService: ArticleApiService,
    private val cache: ArticleCache
) : ArticleRepository {

    override suspend fun getArticles(forceRefresh: Boolean): Result<List<Article>> {
        val cachedEntry = cache.getArticleList()

        if (!forceRefresh && cachedEntry != null && cachedEntry.isFresh()) {
            return Result.Success(
                data = cachedEntry.data.articles.map { it.toArticle() },
                source = Source.CACHE_FRESH
            )
        }

        return when (val result = apiService.getArticles()) {
            is ApiResult.Success -> {
                val articles = result.data.articles
                cache.cacheArticleList(articles.map { it.toCached() })
                Result.Success(articles.map { it.toArticle() }, Source.NETWORK)
            }
            is ApiResult.Error -> {
                if (cachedEntry != null) {
                    val source = when (cachedEntry.age()) {
                        CacheAge.FRESH -> Source.CACHE_FRESH
                        CacheAge.STALE -> Source.CACHE_STALE
                        CacheAge.EXPIRED -> Source.CACHE_EXPIRED
                    }
                    Result.Success(
                        data = cachedEntry.data.articles.map { it.toArticle() },
                        source = source
                    )
                } else {
                    Result.Error(result.error)
                }
            }
        }
    }

    override suspend fun getArticleDetail(id: String, forceRefresh: Boolean): Result<Article> {
        val cachedEntry = cache.getArticleDetail(id)

        if (!forceRefresh && cachedEntry != null && cachedEntry.isFresh()) {
            return Result.Success(
                data = cachedEntry.data.toArticle(),
                source = Source.CACHE_FRESH
            )
        }

        return when (val result = apiService.getArticleDetail(id)) {
            is ApiResult.Success -> {
                val articleDetail = result.data
                cache.cacheArticleDetail(articleDetail.toCached())
                Result.Success(articleDetail.toArticle(), Source.NETWORK)
            }
            is ApiResult.Error -> {
                if (cachedEntry != null) {
                    val source = when (cachedEntry.age()) {
                        CacheAge.FRESH -> Source.CACHE_FRESH
                        CacheAge.STALE -> Source.CACHE_STALE
                        CacheAge.EXPIRED -> Source.CACHE_EXPIRED
                    }
                    Result.Success(
                        data = cachedEntry.data.toArticle(),
                        source = source
                    )
                } else {
                    Result.Error(result.error)
                }
            }
        }
    }

    override suspend fun refreshArticles(): Result<List<Article>> {
        return when (val result = apiService.getArticles()) {
            is ApiResult.Success -> {
                val articles = result.data.articles
                cache.cacheArticleList(articles.map { it.toCached() })
                Result.Success(articles.map { it.toArticle() }, Source.NETWORK)
            }
            is ApiResult.Error -> {
                Result.Error(result.error)
            }
        }
    }

    override fun observeArticles(): Flow<List<Article>?> {
        return cache.observeArticleList().map { entry ->
            entry?.data?.articles?.map { it.toArticle() }
        }
    }

    private fun ArticleDto.toCached() = CachedArticle(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt,
        category = category
    )

    private fun ArticleDetailDto.toCached() = CachedArticle(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt,
        category = category,
    )

    private fun ArticleDetailDto.toArticle() = Article(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt.toLocalDateTime(),
        category = category,
    )

    private fun CachedArticle.toArticle() = Article(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt.toLocalDateTime(),
        category = category
    )

    private fun ArticleDto.toArticle() = Article(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt.toLocalDateTime(),
        category = category
    )

    private fun String.toLocalDateTime(): LocalDateTime {
        val instant = Instant.parse(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())

    }
}
