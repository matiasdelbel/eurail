package com.eurail.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val errorCode: String,
    val errorTitle: String,
    val errorMessage: String
)
