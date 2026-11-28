package com.translator.TalknLearn.model.ui

/**
 * Test helper: resolves a [StatusText] with the English base table, so tests
 * can check the final English line a user would see.
 */
internal fun StatusText?.english(): String? = this?.resolve { BaseUiTexts[it.ordinal] }
