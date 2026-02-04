package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.core.rememberTranslator
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey

@Composable
fun CoinRulesDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    sheetHistoryCount: Int?,
    lastQuizCount: Int?,
    canEarnCoins: Boolean,
    appLanguageState: AppLanguageState,
) {
    if (!isVisible) return

    val t = rememberTranslator(appLanguageState)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.QuizCoinRulesTitle)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(t(UiTextKey.QuizCoinRulesHowToEarn), style = MaterialTheme.typography.titleSmall)
                Text(t(UiTextKey.QuizCoinRule1Coin), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinRuleFirstAttempt), style = MaterialTheme.typography.bodySmall)

                Text(t(UiTextKey.QuizCoinRulesRequirements), style = MaterialTheme.typography.titleSmall)
                Text(t(UiTextKey.QuizCoinRuleMatchMaterials), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinRulePlus10), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinRuleNoDelete), style = MaterialTheme.typography.bodySmall)

                Text(t(UiTextKey.QuizCoinRulesCurrentStatus), style = MaterialTheme.typography.titleSmall)
                Text(
                    t(UiTextKey.QuizCoinRuleMaterialsTemplate).replace("{count}", sheetHistoryCount?.toString() ?: "-"),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    t(UiTextKey.QuizCoinRuleQuizTemplate).replace("{count}", lastQuizCount?.toString() ?: t(UiTextKey.QuizRecordsLabel)),
                    style = MaterialTheme.typography.bodySmall
                )
                if (canEarnCoins) {
                    Text(t(UiTextKey.QuizCoinRulesCanEarn), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                } else if (lastQuizCount != null) {
                    val needed = (lastQuizCount + 10) - (sheetHistoryCount ?: 0)
                    if (needed > 0) {
                        Text(
                            t(UiTextKey.QuizCoinRulesNeedMoreTemplate).replace("{count}", needed.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(t(UiTextKey.QuizCoinRuleGotIt))
            }
        }
    )
}

@Composable
fun QuizRegenConfirmDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    canEarnCoins: Boolean,
    lastQuizCount: Int?,
    sheetHistoryCount: Int?,
    appLanguageState: AppLanguageState,
) {
    if (!isVisible) return

    val t = rememberTranslator(appLanguageState)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.QuizRegenConfirmTitle)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (canEarnCoins)
                        t(UiTextKey.QuizRegenCanEarnCoins)
                    else
                        t(UiTextKey.QuizRegenCannotEarnCoins),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (canEarnCoins) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                if (!canEarnCoins && lastQuizCount != null) {
                    val needed = (lastQuizCount + 10) - (sheetHistoryCount ?: 0)
                    Text(
                        t(UiTextKey.QuizRegenNeedMoreTemplate).replace("{count}", needed.toString()),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    t(UiTextKey.QuizRegenReminder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(t(UiTextKey.QuizRegenGenerateButton))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t(UiTextKey.QuizCancelButton))
            }
        }
    )
}

@Composable
fun CoinsEarnedDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    coinsEarned: Int,
    appLanguageState: AppLanguageState,
) {
    if (!isVisible) return

    val t = rememberTranslator(appLanguageState)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.QuizCoinsEarnedTitle)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    t(UiTextKey.QuizCoinsEarnedMessageTemplate).replace("{coins}", coinsEarned.toString()),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("\n📜 " + t(UiTextKey.QuizCoinRulesTitle), style = MaterialTheme.typography.labelMedium)
                Text(t(UiTextKey.QuizCoinsRule1), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinsRule2), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinsRule3), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinsRule4), style = MaterialTheme.typography.bodySmall)
                Text(t(UiTextKey.QuizCoinsRule5), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(t(UiTextKey.QuizCoinsGreatButton))
            }
        }
    )
}
