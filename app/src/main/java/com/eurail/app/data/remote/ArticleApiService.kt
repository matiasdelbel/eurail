package com.eurail.app.data.remote

import com.eurail.app.data.remote.dto.ErrorDto
import com.eurail.app.domain.Error
import com.eurail.app.data.remote.dto.ArticleDetailDto
import com.eurail.app.data.remote.dto.ArticleDto
import com.eurail.app.data.remote.dto.ArticleListDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.random.Random

interface ArticleApiService {

    suspend fun getArticles(): ApiResult<ArticleListDto>

    suspend fun getArticleDetail(id: String): ApiResult<ArticleDetailDto>
}

sealed class ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>()

    data class Error(val error: com.eurail.app.domain.Error) : ApiResult<Nothing>()
}


class KtorArticleApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.eurail.com",
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val simulateNetworkDelay: Boolean = true
) : ArticleApiService {

    override suspend fun getArticles(): ApiResult<ArticleListDto> {
        return executeRequest {
            httpClient.get("$baseUrl/articles")
        }
    }

    override suspend fun getArticleDetail(id: String): ApiResult<ArticleDetailDto> {
        return executeRequest {
            httpClient.get("$baseUrl/articles/$id")
        }
    }

    private suspend inline fun <reified T> executeRequest(
        crossinline request: suspend () -> HttpResponse
    ): ApiResult<T> {
        return try {
            if (simulateNetworkDelay) {
                delay(Random.nextLong(300, 1200))
            }

            val response = request()
            if (response.status.isSuccess()) {
                ApiResult.Success(data = response.body<T>())
            } else {
                ApiResult.Error(error = parseHttpError(response))
            }
        } catch (exception: Exception) {
            ApiResult.Error(error = parseNetworkException(exception))
        }
    }

    private fun parseNetworkException(e: Exception): Error {
        return when (e) {
            is UnknownHostException -> Error.NetworkError(
                type = Error.NetworkError.Type.NO_INTERNET,
            )

            is SocketTimeoutException -> Error.NetworkError(
                type = Error.NetworkError.Type.TIMEOUT,
            )

            is IOException -> Error.NetworkError(
                type = Error.NetworkError.Type.UNKNOWN,
            )

            else -> Error.UnknownError(e.message ?: "Unknown error")
        }
    }

    private suspend fun parseHttpError(response: HttpResponse): Error {
        val statusCode = response.status.value
        val body = response.bodyAsText()

        if (statusCode in 500..599) {
            return Error.NetworkError(
                type = Error.NetworkError.Type.SERVER_ERROR,
            )
        }

        return try {
            val errorResponse = json.decodeFromString<ErrorDto>(body)
            Error.RemoteError(
                errorCode = errorResponse.errorCode,
                errorTitle = errorResponse.errorTitle,
                errorMessage = errorResponse.errorMessage
            )
        } catch (e: Exception) {
            Error.UnknownError("HTTP $statusCode: $body")
        }
    }
}
