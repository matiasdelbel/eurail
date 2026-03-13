package com.eurail.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.buildkt.material3.tokens.spacers
import com.eurail.app.domain.Article
import com.eurail.app.ui.theme.EurailTheme
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .semantics { contentDescription = "Article: ${article.title}" },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = MaterialTheme.spacers.medium)
        ) {
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )

            Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.extraSmall))

            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))

            Text(
                text = formatDate(article.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun formatDate(updatedAt: LocalDateTime): String {
    return try {
        val month = updatedAt.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val day = updatedAt.dayOfMonth
        val year = updatedAt.year
        "$month $day, $year"
    } catch (_: Exception) {
        updatedAt.toString()
    }
}

@PreviewLightDark
@Composable
private fun Preview_ArticleCard() {
    EurailTheme {
        ArticleCard(
            article = Article(
                id = "1",
                title = "Title",
                summary = "Summary",
                content = "Content",
                category = "Category",
                updatedAt = LocalDateTime.parse(input = "2023-05-01T12:00:00Z"),
            ),
            onClick = {}
        )
    }
}
