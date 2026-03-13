package com.eurail.app.domain

sealed class Result<out T> {

    data class Success<T>(
        val data: T,
        val source: Source
    ) : Result<T>()

    data class Error(
        val error: com.eurail.app.domain.Error,
        val cachedData: Any? = null
    ) : Result<Nothing>()
}

enum class Source {
    NETWORK,
    CACHE_FRESH,
    CACHE_STALE,
    CACHE_EXPIRED
}