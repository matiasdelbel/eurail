package com.eurail.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArticleListDto(
    val articles: List<ArticleDto>
)
