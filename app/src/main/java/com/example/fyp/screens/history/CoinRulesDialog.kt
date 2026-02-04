package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.ui.UiTextKey

/**
 * Reusable coin rules dialog extracted from HistoryScreen.
 * Displays total coins and the rules for earning coins.
 */
@Composable
fun CoinRulesDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    coinTotal: Int,
    t: (UiTextKey) -> String
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.HistoryCoinsDialogTitle)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Coin count display at top
                CoinCountCard(coinTotal = coinTotal)

                HorizontalDivider()

                Text(
                    t(UiTextKey.HistoryCoinRulesTitle),
                    style = MaterialTheme.typography.titleMedium
                )

                // How to earn section
                Text(t(UiTextKey.HistoryCoinHowToEarnTitle), style = MaterialTheme.typography.titleSmall)
                Text(t(UiTextKey.HistoryCoinHowToEarnRule1), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinHowToEarnRule2), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinHowToEarnRule3), style = MaterialTheme.typography.bodySmall)

                // Anti-cheat section
                Text(t(UiTextKey.HistoryCoinAntiCheatTitle), style = MaterialTheme.typography.titleSmall)
                Text(t(UiTextKey.HistoryCoinAntiCheatRule1), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinAntiCheatRule2), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinAntiCheatRule3), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinAntiCheatRule4), style = MaterialTheme.typography.bodySmall)

                // Tips section
                Text(t(UiTextKey.HistoryCoinTipsTitle), style = MaterialTheme.typography.titleSmall)
                Text(t(UiTextKey.HistoryCoinTipsRule1), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.HistoryCoinTipsRule2), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(t(UiTextKey.HistoryCoinGotItButton))
            }
        }
    )
}

/**
 * Reusable coin count card component.
 */
@Composable
fun CoinCountCard(
    coinTotal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$coinTotal",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
