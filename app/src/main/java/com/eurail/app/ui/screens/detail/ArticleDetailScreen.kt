package com.eurail.app.ui.screens.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.buildkt.material3.tokens.spacers
import com.eurail.app.R
import com.eurail.app.ui.components.ErrorScreen
import com.eurail.app.ui.components.LoadingScreen
import com.eurail.app.ui.components.MarkdownText
import com.eurail.app.ui.screens.detail.ArticleDetailUiState.Error
import com.eurail.app.ui.screens.detail.ArticleDetailUiState.Loading
import com.eurail.app.ui.screens.detail.ArticleDetailUiState.Success
import com.eurail.app.ui.theme.EurailTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState is Success) uiState.title else stringResource(id = R.string.article),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    val iconContentDescription = stringResource(R.string.navigate_back)
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics { 
                            contentDescription = iconContentDescription
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (uiState is Success) {
                        val iconContentDescription = stringResource(R.string.refresh_article)
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.semantics {
                                contentDescription = iconContentDescription
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState is Success && uiState.isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Crossfade(
                targetState = uiState,
                label = "detail_content"
            ) { currentState ->
                when (currentState) {
                    is Error -> ErrorScreen(
                        error = currentState.error,
                        onRetry = onRetry
                    )

                    is Loading -> LoadingScreen()


                    is Success -> ArticleContent(
                        category = currentState.category,
                        title = currentState.title,
                        updatedAt = currentState.updatedAt,
                        content = currentState.content,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleContent(
    category: String,
    title: String,
    updatedAt: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(all = MaterialTheme.spacers.medium)
    ) {
        SuggestionChip(
            onClick = {},
            label = {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))

        Text(
            text = "Last updated: $updatedAt",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.large))

        MarkdownText(content = content)
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleDetailScreen_Error() {
    EurailTheme {
        ArticleDetailScreen(
            uiState = Error(
                error = com.eurail.app.domain.Error.UnknownError(
                    message = "Something went wrong"
                ),
            ),
            onNavigateBack = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleDetailScreen_Loading() {
    EurailTheme {
        ArticleDetailScreen(
            uiState = Loading,
            onNavigateBack = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleDetailScreen_Success() {
    EurailTheme {
        ArticleDetailScreen(
            uiState = Success(
                title = "Getting Started with Your Rail Pass",
                summary = "Learn how to activate and use your Eurail pass for the first time.",
                content = "This is the content of the article.",
                updatedAt = "2024-03-10T14:30:00Z",
                category = "Getting Started",
                isRefreshing = false,
            ),
            onNavigateBack = {},
            onRetry = {},
            onRefresh = {},
        )
    }
}