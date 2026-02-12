package com.example.fyp.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.theme.ALL_PALETTES
import com.example.fyp.ui.theme.hexStringToColor

@Composable
fun ColorPaletteSelector(
    currentPaletteId: String,
    unlockedPalettes: List<String>,
    coinBalance: Int = 0,
    onPaletteSelected: (String) -> Unit,
    onUnlockClicked: (String, Int) -> Unit,
    unlockError: String? = null,
    t: (UiTextKey) -> String
) {
    var pendingUnlockPalette by remember { mutableStateOf<String?>(null) }

    // Unlock confirmation dialog
    pendingUnlockPalette?.let { paletteId ->
        val palette = ALL_PALETTES.find { it.id == paletteId }
        palette?.let {
            AlertDialog(
                onDismissRequest = { pendingUnlockPalette = null },
                title = { Text("Unlock ${it.name}?") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Cost: ${it.cost} coins")
                        Text("Your coins: $coinBalance")
                        if (coinBalance < it.cost) {
                            Text(
                                "❌ Insufficient coins!",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUnlockClicked(paletteId, it.cost)
                            pendingUnlockPalette = null
                        },
                        enabled = coinBalance >= it.cost
                    ) {
                        Text("Unlock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingUnlockPalette = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    // Error snackbar
    unlockError?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = "⚠️ $it",
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(ALL_PALETTES, key = { it.id }) { palette ->
            ColorPaletteCard(
                palette = palette,
                isSelected = currentPaletteId == palette.id,
                isUnlocked = unlockedPalettes.contains(palette.id),
                onSelect = { onPaletteSelected(palette.id) },
                onUnlock = { pendingUnlockPalette = palette.id },
                t = t
            )
        }
    }
}

@Composable
private fun ColorPaletteCard(
    palette: com.example.fyp.ui.theme.ColorPalette,
    isSelected: Boolean,
    isUnlocked: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit,
    t: (UiTextKey) -> String
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(enabled = isUnlocked) { onSelect() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Color preview circles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = hexStringToColor(palette.lightPrimary),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = hexStringToColor(palette.lightSecondary),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = hexStringToColor(palette.lightTertiary),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }

            // Palette name
            Text(
                text = palette.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Button
            when {
                isSelected -> {
                    Text(
                        text = t(UiTextKey.SettingsColorAlreadyUnlocked),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                isUnlocked -> {
                    Button(
                        onClick = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsColorSelectButton),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> {
                    Button(
                        onClick = onUnlock,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsColorCostTemplate).replace("{cost}", "${palette.cost}"),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
