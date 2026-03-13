package com.eurail.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailDto(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val updatedAt: String,
    val category: String
)
