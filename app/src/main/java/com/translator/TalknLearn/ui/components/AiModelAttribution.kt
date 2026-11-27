package com.translator.TalknLearn.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.translator.TalknLearn.R

private val lunaModel = Regex("gpt-6-luna(?:-(\\d{4}-\\d{2}-\\d{2}))?", RegexOption.IGNORE_CASE)

@Composable
@Suppress("FunctionNaming")
fun AiModelAttribution(
    model: String?,
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int = R.string.ai_model_attribution
) {
    val modelId = model?.trim()?.takeIf { it.isNotEmpty() } ?: return
    val lunaMatch = lunaModel.matchEntire(modelId)
    val displayName = if (lunaMatch == null) {
        modelId
    } else {
        val name = stringResource(R.string.ai_model_gpt_6_luna)
        val version = lunaMatch.groups[1]?.value
        if (version == null) name else stringResource(R.string.ai_model_with_version, name, version)
    }
    Text(
        text = stringResource(labelRes, displayName),
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
