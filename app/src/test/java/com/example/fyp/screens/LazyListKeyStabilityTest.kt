package com.example.fyp.screens

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

// Static regression guard for docs/APP_SUGGESTIONS.md section 4.7.
// Every dynamic items(collection, ...) call under
// app/src/main/java/com/example/fyp/screens must declare a stable key = lambda
// for predictable Compose recomposition and animations.
// Pure integer-count placeholders such as items(3) skeletons are exempt.
class LazyListKeyStabilityTest {

    @Test
    fun every_dynamic_items_call_supplies_stable_key() {
        val screensRoot = File("src/main/java/com/example/fyp/screens")
        assertTrue("screens directory not found at " + screensRoot.absolutePath, screensRoot.isDirectory)

        val itemsCallRegex = Regex("\\bitems\\(([^)]*)\\)")
        val numericArgRegex = Regex("\\d+")
        val violations = mutableListOf<String>()

        screensRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                file.useLines { lines ->
                    lines.forEachIndexed { idx, line ->
                        val trimmed = line.trim()
                        if (trimmed.startsWith("//") || trimmed.startsWith("*")) return@forEachIndexed
                        val match = itemsCallRegex.find(trimmed) ?: return@forEachIndexed
                        val args = match.groupValues[1]
                        val firstArg = args.substringBefore(',').trim()
                        if (firstArg.matches(numericArgRegex)) return@forEachIndexed
                        if (!args.contains("key")) {
                            violations += file.path + ":" + (idx + 1) + ": " + trimmed
                        }
                    }
                }
            }

        assertTrue(
            "items(...) without key = found:\n  - " + violations.joinToString("\n  - "),
            violations.isEmpty()
        )
    }
}
