package com.eurail.shared.cache

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ArticleCacheTest {

    private fun createTestArticle(id: String = "1") = CachedArticle(
        id = id,
        title = "Test Article $id",
        summary = "Summary for article $id",
        content = "Full content for article $id",
        updatedAt = "2024-01-15T10:00:00Z",
        category = "General"
    )

    @Test
    fun `cache returns null when empty`() = runTest {
        val cache = InMemoryArticleCache()
        assertNull(cache.getArticleList())
        assertNull(cache.getArticleDetail("1"))
    }

    @Test
    fun `cache stores and retrieves article list`() = runTest {
        val cache = InMemoryArticleCache()
        val articles = listOf(createTestArticle("1"), createTestArticle("2"))

        cache.cacheArticleList(articles)

        val result = cache.getArticleList()
        assertNotNull(result)
        assertEquals(2, result.data.articles.size)
        assertEquals("Test Article 1", result.data.articles[0].title)
    }

    @Test
    fun `caching list also caches individual articles`() = runTest {
        val cache = InMemoryArticleCache()
        val articles = listOf(createTestArticle("1"), createTestArticle("2"))

        cache.cacheArticleList(articles)

        val detail = cache.getArticleDetail("1")
        assertNotNull(detail)
        assertEquals("Test Article 1", detail.data.title)
    }

    @Test
    fun `cache entry is fresh within 5 minutes`() {
        val now = Clock.System.now()
        val entry = CacheEntry(
            data = CachedArticleList(listOf(createTestArticle())),
            cachedAt = now - 4.minutes,
            key = "test"
        )

        assertTrue(entry.isFresh(now))
        assertFalse(entry.isStale(now))
        assertFalse(entry.isExpired(now))
        assertEquals(CacheAge.FRESH, entry.age(now))
    }

    @Test
    fun `cache entry is stale after 5 minutes but before 24 hours`() {
        val now = Clock.System.now()
        val entry = CacheEntry(
            data = CachedArticleList(listOf(createTestArticle())),
            cachedAt = now - 2.hours,
            key = "test"
        )

        assertFalse(entry.isFresh(now))
        assertTrue(entry.isStale(now))
        assertFalse(entry.isExpired(now))
        assertEquals(CacheAge.STALE, entry.age(now))
    }

    @Test
    fun `cache entry is expired after 24 hours`() {
        val now = Clock.System.now()
        val entry = CacheEntry(
            data = CachedArticleList(listOf(createTestArticle())),
            cachedAt = now - 25.hours,
            key = "test"
        )

        assertFalse(entry.isFresh(now))
        assertFalse(entry.isStale(now))
        assertTrue(entry.isExpired(now))
        assertEquals(CacheAge.EXPIRED, entry.age(now))
    }

    @Test
    fun `shouldRefresh returns true when cache is empty`() {
        val cache = InMemoryArticleCache()
        assertTrue(cache.shouldRefresh())
    }

    @Test
    fun `shouldRefresh returns false when cache is fresh`() = runTest {
        val fixedClock = object : Clock {
            override fun now(): Instant = Instant.parse("2024-01-15T12:00:00Z")
        }
        val cache = InMemoryArticleCache(clock = fixedClock)
        cache.cacheArticleList(listOf(createTestArticle()))

        assertFalse(cache.shouldRefresh())
    }

    @Test
    fun `shouldRefresh returns true when cache is stale`() = runTest {
        val baseTime = Instant.parse("2024-01-15T12:00:00Z")
        var currentTime = baseTime

        val testClock = object : Clock {
            override fun now(): Instant = currentTime
        }

        val cache = InMemoryArticleCache(clock = testClock)
        cache.cacheArticleList(listOf(createTestArticle()))

        currentTime = baseTime + 10.minutes

        assertTrue(cache.shouldRefresh())
    }

    @Test
    fun `clear removes all cached data`() = runTest {
        val cache = InMemoryArticleCache()
        cache.cacheArticleList(listOf(createTestArticle()))

        assertNotNull(cache.getArticleList())

        cache.clear()

        assertNull(cache.getArticleList())
        assertNull(cache.getArticleDetail("1"))
    }
}
