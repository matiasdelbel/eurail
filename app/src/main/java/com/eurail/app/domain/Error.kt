package com.eurail.app.domain

sealed class Error {

    data class NetworkError(
        val type: Type
    ) : Error() {

        enum class Type {
            NO_INTERNET,
            TIMEOUT,
            SERVER_ERROR,
            UNKNOWN
        }
    }

    data class RemoteError(
        val errorCode: String,
        val errorTitle: String,
        val errorMessage: String
    ) : Error()

    data class UnknownError(
        val message: String
    ) : Error()
}
