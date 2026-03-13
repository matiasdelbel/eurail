package com.eurail.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier
) {
    val lines = remember(content) { content.lines() }

    Column(modifier = modifier) {
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                line.startsWith("> ") -> {
                    BlockQuote(text = line.removePrefix("> "))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    BulletPoint(text = parseInlineMarkdown(line.drop(2)))
                    Spacer(modifier = Modifier.height(4.dp))
                }
                line.matches(Regex("^\\d+\\.\\s.*")) -> {
                    val numberMatch = Regex("^(\\d+)\\.\\s(.*)").find(line)
                    if (numberMatch != null) {
                        NumberedPoint(
                            number = numberMatch.groupValues[1],
                            text = parseInlineMarkdown(numberMatch.groupValues[2])
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                line.startsWith("- [ ] ") || line.startsWith("- [x] ") -> {
                    val isChecked = line.startsWith("- [x] ")
                    ChecklistItem(
                        text = line.drop(6),
                        isChecked = isChecked
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                line.startsWith("```") -> {
                    val codeLines = mutableListOf<String>()
                    i++
                    while (i < lines.size && !lines[i].startsWith("```")) {
                        codeLines.add(lines[i])
                        i++
                    }
                    CodeBlock(code = codeLines.joinToString("\n"))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                line.startsWith("|") && line.endsWith("|") -> {
                    val tableLines = mutableListOf(line)
                    i++
                    while (i < lines.size && lines[i].startsWith("|") && lines[i].endsWith("|")) {
                        tableLines.add(lines[i])
                        i++
                    }
                    i--
                    MarkdownTable(tableLines)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                line.isBlank() -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                line.startsWith("---") || line.startsWith("***") -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                else -> {
                    Text(
                        text = parseInlineMarkdown(line),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            i++
        }
    }
}

@Composable
private fun BlockQuote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontStyle = FontStyle.Italic
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BulletPoint(text: androidx.compose.ui.text.AnnotatedString) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NumberedPoint(number: String, text: androidx.compose.ui.text.AnnotatedString) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ChecklistItem(text: String, isChecked: Boolean) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        Text(
            text = if (isChecked) "☑" else "☐",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isChecked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MarkdownTable(lines: List<String>) {
    if (lines.size < 2) return

    val headers = lines[0].split("|").filter { it.isNotBlank() }.map { it.trim() }
    val rows = lines.drop(2).map { row ->
        row.split("|").filter { it.isNotBlank() }.map { it.trim() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Row {
            headers.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        rows.forEach { row ->
            Row {
                row.forEach { cell ->
                    Text(
                        text = cell,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

private fun parseInlineMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var remaining = text
        while (remaining.isNotEmpty()) {
            when {
                remaining.startsWith("**") -> {
                    val endIndex = remaining.indexOf("**", startIndex = 2)
                    if (endIndex != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(remaining.substring(2, endIndex))
                        }
                        remaining = remaining.substring(endIndex + 2)
                    } else {
                        append("**")
                        remaining = remaining.substring(2)
                    }
                }
                remaining.startsWith("*") || remaining.startsWith("_") -> {
                    val marker = remaining[0]
                    val endIndex = remaining.indexOf(marker, startIndex = 1)
                    if (endIndex != -1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(remaining.substring(1, endIndex))
                        }
                        remaining = remaining.substring(endIndex + 1)
                    } else {
                        append(marker)
                        remaining = remaining.substring(1)
                    }
                }
                remaining.startsWith("`") -> {
                    val endIndex = remaining.indexOf("`", startIndex = 1)
                    if (endIndex != -1) {
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                            append(remaining.substring(1, endIndex))
                        }
                        remaining = remaining.substring(endIndex + 1)
                    } else {
                        append("`")
                        remaining = remaining.substring(1)
                    }
                }
                else -> {
                    val nextSpecial = listOf(
                        remaining.indexOf("**"),
                        remaining.indexOf("*"),
                        remaining.indexOf("_"),
                        remaining.indexOf("`")
                    ).filter { it > 0 }.minOrNull() ?: remaining.length

                    append(remaining.substring(0, nextSpecial))
                    remaining = remaining.substring(nextSpecial)
                }
            }
        }
    }
}
