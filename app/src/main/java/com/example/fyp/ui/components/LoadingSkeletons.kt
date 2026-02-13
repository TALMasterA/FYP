package com.example.fyp.ui.components

/**
 * Loading skeleton components with shimmer effects.
 *
 * These are utility components ready for integration into screens.
 * "Unused" warnings are expected until integrated.
 *
 * Usage: Import and use when loading data in screens.
 * Example:
 * ```
 * if (isLoading) {
 *     TranslationCardSkeleton()
 * } else {
 *     ActualContent()
 * }
 * ```
 */

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect modifier for loading skeletons.
 * Creates an animated gradient that moves across the composable.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
            end = Offset(translateAnim, translateAnim)
        )
    )
}

/**
 * Generic loading box with shimmer effect.
 */
@Composable
fun LoadingBox(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 16.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .shimmerEffect()
    )
}

/**
 * Loading skeleton for a translation/favorite card.
 * Mimics the structure of actual content cards.
 */
@Composable
fun TranslationCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language pair indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LoadingBox(modifier = Modifier.width(80.dp), height = 14.dp)
                LoadingBox(modifier = Modifier.width(60.dp), height = 14.dp)
            }

            // Source text
            LoadingBox(modifier = Modifier.fillMaxWidth(0.9f), height = 18.dp)
            LoadingBox(modifier = Modifier.fillMaxWidth(0.7f), height = 18.dp)

            Spacer(modifier = Modifier.height(4.dp))

            // Target text
            LoadingBox(modifier = Modifier.fillMaxWidth(0.85f), height = 18.dp)
            LoadingBox(modifier = Modifier.fillMaxWidth(0.6f), height = 18.dp)

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LoadingBox(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(8.dp))
                LoadingBox(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(8.dp))
                LoadingBox(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(8.dp))
            }
        }
    }
}

/**
 * Loading skeleton for word bank items.
 */
@Composable
fun WordBankItemSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Word
            LoadingBox(modifier = Modifier.width(120.dp), height = 20.dp)

            // Pronunciation
            LoadingBox(modifier = Modifier.width(100.dp), height = 14.dp)

            // Translation
            LoadingBox(modifier = Modifier.fillMaxWidth(0.8f), height = 16.dp)

            Spacer(modifier = Modifier.height(4.dp))

            // Example sentence
            LoadingBox(modifier = Modifier.fillMaxWidth(0.95f), height = 14.dp)
            LoadingBox(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)
        }
    }
}

/**
 * Loading skeleton for learning sheet content.
 */
@Composable
fun LearningSheetSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        LoadingBox(modifier = Modifier.width(200.dp), height = 24.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Content paragraphs
        repeat(5) {
            LoadingBox(modifier = Modifier.fillMaxWidth(0.95f), height = 16.dp)
            LoadingBox(modifier = Modifier.fillMaxWidth(0.9f), height = 16.dp)
            LoadingBox(modifier = Modifier.fillMaxWidth(0.85f), height = 16.dp)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Loading skeleton for quiz questions.
 */
@Composable
fun QuizQuestionSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question number
        LoadingBox(modifier = Modifier.width(100.dp), height = 18.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Question text
        LoadingBox(modifier = Modifier.fillMaxWidth(0.9f), height = 20.dp)
        LoadingBox(modifier = Modifier.fillMaxWidth(0.7f), height = 20.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Answer options
        repeat(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                LoadingBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    height = 16.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

