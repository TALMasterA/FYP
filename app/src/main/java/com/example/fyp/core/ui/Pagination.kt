package com.example.fyp.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

    // Centre a compact row so the buttons aren't stretched across the full screen width
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPrev, enabled = page > 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = prevLabel,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(pageText)
            IconButton(onClick = onNext, enabled = page < totalPages - 1) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = nextLabel,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}