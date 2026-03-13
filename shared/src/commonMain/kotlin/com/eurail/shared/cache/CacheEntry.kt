package com.eurail.shared.cache

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a cached item with metadata for TTL/staleness management.
 *
 * Staleness Rule:
 * - Data is considered FRESH for 5 minutes (can serve without network check)
 * - Data is considered STALE after 5 minutes but before 24 hours (serve but refresh in background)
 * - Data is considered EXPIRED after 24 hours (must refresh, but can still serve if offline)
 */
@Serializable
data class CacheEntry<T>(
    val data: T,
    val cachedAt: Instant,
    val key: String
) {
    companion object {
        const val FRESH_DURATION_MINUTES = 5L
        const val STALE_DURATION_HOURS = 24L
    }

    fun isFresh(now: Instant = Clock.System.now()): Boolean {
        val ageMinutes = (now - cachedAt).inWholeMinutes
        return ageMinutes < FRESH_DURATION_MINUTES
    }

    fun isStale(now: Instant = Clock.System.now()): Boolean {
        val ageMinutes = (now - cachedAt).inWholeMinutes
        return ageMinutes >= FRESH_DURATION_MINUTES && !isExpired(now)
    }

    fun isExpired(now: Instant = Clock.System.now()): Boolean {
        val ageHours = (now - cachedAt).inWholeHours
        return ageHours >= STALE_DURATION_HOURS
    }

    fun age(now: Instant = Clock.System.now()): CacheAge {
        return when {
            isFresh(now) -> CacheAge.FRESH
            isStale(now) -> CacheAge.STALE
            else -> CacheAge.EXPIRED
        }
    }
}

enum class CacheAge {
    FRESH,
    STALE,
    EXPIRED
}
