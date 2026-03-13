package com.eurail.app.domain

import kotlinx.datetime.LocalDateTime

data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val updatedAt: LocalDateTime,
    val category: String
)
