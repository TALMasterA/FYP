package com.translator.TalknLearn.model.ui

/**
 * Combined BaseUiTexts that merges Core and Screen-specific strings.
 * CRITICAL: Order must exactly match UiTextKey enum order!
 */
val BaseUiTexts: List<String>
    get() = CoreUiTexts + ScreenUiTexts