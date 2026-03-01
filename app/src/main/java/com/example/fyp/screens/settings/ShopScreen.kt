package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.delay
import com.example.fyp.core.UiConstants
import com.example.fyp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPurchaseConfirmDialog by remember { mutableStateOf(false) }

    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // Purchase confirmation dialog - shown before purchase
    if (showPurchaseConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPurchaseConfirmDialog = false },
            title = { Text(t(UiTextKey.ShopHistoryExpandedTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        t(UiTextKey.ShopHistoryExpandedMessage).replace(
                            "{limit}",
                            (uiState.currentHistoryLimit + UserSettings.HISTORY_EXPANSION_INCREMENT).coerceAtMost(UserSettings.MAX_HISTORY_LIMIT).toString()
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showPurchaseConfirmDialog = false
                    viewModel.expandHistoryLimit()
                }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseConfirmDialog = false }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    // Auto-dismiss purchase error after delay
    LaunchedEffect(uiState.purchaseError) {
        if (uiState.purchaseError != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearPurchaseError()
        }
    }

    // Auto-dismiss unlock error after delay
    LaunchedEffect(uiState.unlockError) {
        if (uiState.unlockError != null) {
            delay(UiConstants.COIN_UNLOCK_SUCCESS_DURATION_MS)
            viewModel.clearUnlockError()
        }
    }

    // Auto-dismiss success message after delay
    LaunchedEffect(uiState.purchaseSuccess) {
        if (uiState.purchaseSuccess != null) {
            delay(UiConstants.SUCCESS_MESSAGE_DURATION_MS)
            viewModel.clearPurchaseSuccess()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.ShopTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.large)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraLarge)
        ) {
            // Coin balance display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.large),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = t(UiTextKey.ShopCoinBalance),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${uiState.coinBalance} 🪙",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // History View Expansion Section
            Text(
                text = t(UiTextKey.ShopHistoryExpansionTitle),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.large),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                ) {
                    Text(
                        text = t(UiTextKey.ShopHistoryExpansionDesc),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t(UiTextKey.ShopCurrentLimit).replace(
                                "{limit}",
                                uiState.currentHistoryLimit.toString()
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${t(UiTextKey.ShopMaxLimit)} ${UserSettings.MAX_HISTORY_LIMIT}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (uiState.currentHistoryLimit < UserSettings.MAX_HISTORY_LIMIT) {
                        val canAfford = uiState.coinBalance >= UserSettings.HISTORY_EXPANSION_COST

                        Button(
                            onClick = { showPurchaseConfirmDialog = true },
                            enabled = canAfford && !uiState.isPurchasing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isPurchasing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = t(UiTextKey.ShopBuyHistoryExpansion).replace(
                                        "{cost}",
                                        UserSettings.HISTORY_EXPANSION_COST.toString()
                                    ).replace(
                                        "{increment}",
                                        UserSettings.HISTORY_EXPANSION_INCREMENT.toString()
                                    )
                                )
                            }
                        }

                        if (!canAfford) {
                            Text(
                                text = t(UiTextKey.ShopInsufficientCoins),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        Text(
                            text = t(UiTextKey.ShopMaxLimitReached),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Color Palette Section
            Text(
                text = t(UiTextKey.ShopColorPaletteTitle),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.large),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                ) {
                    Text(
                        text = t(UiTextKey.ShopColorPaletteDesc),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    ColorPaletteSelector(
                        currentPaletteId = uiState.currentPaletteId,
                        unlockedPalettes = uiState.unlockedPalettes,
                        coinBalance = uiState.coinBalance,
                        onPaletteSelected = { viewModel.selectPalette(it) },
                        onUnlockClicked = { paletteId, cost -> viewModel.unlockPalette(paletteId, cost) },
                        unlockError = uiState.unlockError,
                        t = t
                    )
                }
            }

            // Purchase Error
            uiState.purchaseError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(AppSpacing.large)
                    )
                }
            }
        }
    }
}

