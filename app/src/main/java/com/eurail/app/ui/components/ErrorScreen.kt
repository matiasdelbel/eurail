package com.eurail.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.buildkt.material3.tokens.spacers
import com.eurail.app.domain.Error
import com.eurail.app.domain.Error.NetworkError
import com.eurail.app.domain.Error.RemoteError
import com.eurail.app.domain.Error.UnknownError
import com.eurail.app.ui.theme.EurailTheme

@Composable
fun ErrorScreen(
    error: Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Error screen with retry option" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when (error) {
                is NetworkError -> when (error.type) {
                    NetworkError.Type.NO_INTERNET -> Icons.Default.SignalWifiOff
                    NetworkError.Type.TIMEOUT -> Icons.Default.TimerOff
                    else -> Icons.Default.CloudOff
                }
                is RemoteError -> Icons.Default.Error
                is UnknownError -> Icons.Default.Warning
            },
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.medium))

        Text(
            text = when (error) {
                is NetworkError -> when (error.type) {
                    NetworkError.Type.NO_INTERNET -> "No Connection"
                    NetworkError.Type.TIMEOUT -> "Request Timeout"
                    NetworkError.Type.SERVER_ERROR -> "Server Error"
                    NetworkError.Type.UNKNOWN -> "Network Error"
                }
                is RemoteError -> error.errorTitle
                is UnknownError -> "Error"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))

        Text(
            text = when (error) {
                is NetworkError -> when (error.type) {
                    NetworkError.Type.NO_INTERNET -> "No internet connection. Please check your network settings."
                    NetworkError.Type.TIMEOUT -> "Request timed out. Please try again."
                    NetworkError.Type.SERVER_ERROR -> "Server is temporarily unavailable. Please try again later."
                    NetworkError.Type.UNKNOWN -> "A network error occurred."
                }
                is RemoteError -> error.errorMessage
                is UnknownError -> error.message
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacers.medium)
        )

        if (error is Error.RemoteError) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacers.small))
            Text(
                text = "Error code: ${error.errorCode}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.large))

        Button(
            onClick = onRetry,
            modifier = Modifier.semantics { contentDescription = "Retry button" }
        ) {
            Text("Retry")
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_ErrorScreen_RemoteError() {
    MaterialTheme {
        ErrorScreen(
            error = RemoteError(
                errorCode = "404",
                errorTitle = "Not Found",
                errorMessage = "The requested resource was not found."
            ),
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ErrorScreen_NetworkError(
    @PreviewParameter(NetworkErrorTypePreviewProvider::class) errorType: NetworkError.Type
) {
    EurailTheme {
        ErrorScreen(
            error = NetworkError(
                type = errorType,
            ),
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ErrorScreen_UnknownError() {
    EurailTheme {
        ErrorScreen(
            error = UnknownError(
                message = "An unknown error occurred."
            ),
            onRetry = {},
        )
    }
}

private class NetworkErrorTypePreviewProvider : PreviewParameterProvider<NetworkError.Type> {
    override val values = sequenceOf(
        NetworkError.Type.NO_INTERNET,
        NetworkError.Type.TIMEOUT,
        NetworkError.Type.SERVER_ERROR,
        NetworkError.Type.UNKNOWN
    )
}
