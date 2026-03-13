package com.eurail.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.eurail.app.domain.Error
import com.eurail.app.ui.components.ErrorScreen
import com.eurail.app.ui.theme.EurailTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ErrorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorScreen_displaysNetworkError_andRetryWorks() {
        var retryClicked = false
        val networkError = Error.NetworkError(
            type = Error.NetworkError.Type.NO_INTERNET,
        )

        composeTestRule.setContent {
            EurailTheme {
                ErrorScreen(
                    error = networkError,
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("No Connection").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            "No internet connection. Please check your network settings."
        ).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Retry button")
            .assertIsDisplayed()
            .performClick()

        assertTrue("Retry callback should be triggered", retryClicked)
    }

    @Test
    fun errorScreen_displaysBackendError_withErrorCode() {
        var retryClicked = false
        val remoteError = Error.RemoteError(
            errorCode = "RATE_LIMIT_EXCEEDED",
            errorTitle = "Too Many Requests",
            errorMessage = "Please wait a moment before trying again."
        )

        composeTestRule.setContent {
            EurailTheme {
                ErrorScreen(
                    error = remoteError,
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Too Many Requests").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            "Please wait a moment before trying again."
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText("Error code: RATE_LIMIT_EXCEEDED").assertIsDisplayed()

        composeTestRule.onNodeWithText("Retry").performClick()

        assertTrue("Retry callback should be triggered", retryClicked)
    }

    @Test
    fun errorScreen_displaysTimeoutError() {
        val timeoutError = Error.NetworkError(
            type = Error.NetworkError.Type.TIMEOUT,
        )

        composeTestRule.setContent {
            EurailTheme {
                ErrorScreen(
                    error = timeoutError,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Request Timeout").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            "Request timed out. Please try again."
        ).assertIsDisplayed()
    }

    @Test
    fun errorScreen_displaysServerError() {
        val serverError = Error.NetworkError(
            type = Error.NetworkError.Type.SERVER_ERROR,
        )

        composeTestRule.setContent {
            EurailTheme {
                ErrorScreen(
                    error = serverError,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Server Error").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            "Server is temporarily unavailable. Please try again later."
        ).assertIsDisplayed()
    }

    @Test
    fun errorScreen_hasAccessibleRetryButton() {
        composeTestRule.setContent {
            EurailTheme {
                ErrorScreen(
                    error = Error.UnknownError("Something went wrong"),
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Error screen with retry option")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Retry button")
            .assertIsDisplayed()
    }
}
