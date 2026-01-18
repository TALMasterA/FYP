package com.example.fyp.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

fun pageCount(total: Int, pageSize: Int): Int =
    if (total <= 0) 1 else ((total - 1) / pageSize) + 1

@Composable
fun PaginationRow(
    page: Int,
    totalPages: Int,
    prevLabel: String,
    nextLabel: String,
    pageLabelTemplate: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageText = pageLabelTemplate
        .replace("{page}", (page + 1).toString())
        .replace("{total}", totalPages.toString())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onPrev, enabled = page > 0) { Text(prevLabel) }
        Text(pageText)
        TextButton(onClick = onNext, enabled = page < totalPages - 1) { Text(nextLabel) }
    }
}