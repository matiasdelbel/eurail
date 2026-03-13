package com.eurail.shared.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

interface ArticleCache {

    suspend fun getArticleList(): CacheEntry<CachedArticleList>?

    suspend fun getArticleDetail(id: String): CacheEntry<CachedArticle>?

    suspend fun cacheArticleList(articles: List<CachedArticle>)

    suspend fun cacheArticleDetail(article: CachedArticle)

    suspend fun clear()

    fun observeArticleList(): Flow<CacheEntry<CachedArticleList>?>

    fun shouldRefresh(): Boolean
}

class InMemoryArticleCache(
    private val clock: Clock = Clock.System
) : ArticleCache {

    private val mutex = Mutex()
    private var articleListEntry: CacheEntry<CachedArticleList>? = null
    private val articleDetailEntries = mutableMapOf<String, CacheEntry<CachedArticle>>()
    private val articleListFlow = MutableStateFlow<CacheEntry<CachedArticleList>?>(null)

    override suspend fun getArticleList(): CacheEntry<CachedArticleList>? = mutex.withLock {
        articleListEntry
    }

    override suspend fun getArticleDetail(id: String): CacheEntry<CachedArticle>? = mutex.withLock {
        articleDetailEntries[id]
    }

    override suspend fun cacheArticleList(articles: List<CachedArticle>) = mutex.withLock {
        val entry = CacheEntry(
            data = CachedArticleList(articles),
            cachedAt = clock.now(),
            key = ARTICLE_LIST_KEY
        )
        articleListEntry = entry
        articleListFlow.value = entry

        articles.forEach { article ->
            articleDetailEntries[article.id] = CacheEntry(
                data = article,
                cachedAt = clock.now(),
                key = "article_${article.id}"
            )
        }
    }

    override suspend fun cacheArticleDetail(article: CachedArticle) = mutex.withLock {
        articleDetailEntries[article.id] = CacheEntry(
            data = article,
            cachedAt = clock.now(),
            key = "article_${article.id}"
        )
    }

    override suspend fun clear() = mutex.withLock {
        articleListEntry = null
        articleDetailEntries.clear()
        articleListFlow.value = null
    }

    override fun observeArticleList(): Flow<CacheEntry<CachedArticleList>?> = articleListFlow

    override fun shouldRefresh(): Boolean {
        val entry = articleListEntry ?: return true
        return !entry.isFresh(clock.now())
    }

    companion object {
        const val ARTICLE_LIST_KEY = "article_list"
    }
}

@Serializable
data class CachedArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val updatedAt: String,
    val category: String
)

@Serializable
data class CachedArticleList(
    val articles: List<CachedArticle>
)
