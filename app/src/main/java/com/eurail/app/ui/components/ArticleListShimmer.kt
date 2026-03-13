package com.eurail.app.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.buildkt.material3.tokens.spacers
import com.eurail.app.ui.theme.EurailTheme

@Composable
fun ArticleListShimmer(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacers.medium)
    ) {
        repeat(itemCount) {
            ShimmerArticleItem()
            Spacer(modifier = Modifier.height(MaterialTheme.spacers.small))
        }
    }
}

@Composable
fun ShimmerArticleItem() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200)
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 200f, translateAnim + 200f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacers.small)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(MaterialTheme.spacers.extraSmall))
                    .background(brush)
            )
        }
        Spacer(modifier = Modifier.height(height = MaterialTheme.spacers.small))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(24.dp)
                .clip(RoundedCornerShape(MaterialTheme.spacers.extraSmall))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacers.small))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacers.medium)
                .clip(RoundedCornerShape(MaterialTheme.spacers.extraSmall))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacers.extraSmall))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(MaterialTheme.spacers.medium)
                .clip(RoundedCornerShape(MaterialTheme.spacers.extraSmall))
                .background(brush)
        )
    }
}
@PreviewLightDark
@Composable
private fun Preview_ArticleListShimmer() {
    EurailTheme {
        ArticleListShimmer(
            itemCount = 10,
        )
    }
}