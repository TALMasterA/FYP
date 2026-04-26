package com.translator.TalknLearn.model

import org.junit.Test
import java.io.File

/**
 * Static guard for §5.4: every Kotlin Boolean property declared as `val isXxx: Boolean`
 * inside a Firestore-mapped data class under `model/` MUST carry an explicit
 * `@PropertyName("isXxx")` annotation. Without it, Firestore's POJO mapper strips
 * the `is` prefix from the JavaBean getter `isXxx()` and silently reads/writes the
 * field under the key `xxx`, which can cause data to default to false on read while
 * other call sites write `isXxx`.
 *
 * Files annotated with `@Serializable` are treated as kotlinx-JSON-serialized models
 * (not Firestore POJOs) and are skipped — the annotation is irrelevant for them.
 */
class PropertyNameAnnotationTest {

    private val isPropRegex = Regex("""\bval\s+(is[A-Z]\w*)\s*:\s*Boolean""")

    @Test
    fun every_is_prefixed_boolean_in_firestore_model_has_propertyname_annotation() {
        val modelDir = File("src/main/java/com/translator/TalknLearn/model")
        check(modelDir.exists()) { "Model directory missing: ${modelDir.absolutePath}" }

        val violations = mutableListOf<String>()

        modelDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val text = file.readText()
                // Skip kotlinx-serialized models — they don't go through Firestore POJO mapping.
                if (text.contains("@Serializable")) return@forEach

                val lines = text.lines()
                lines.forEachIndexed { idx, line ->
                    val trimmed = line.trim()
                    if (trimmed.startsWith("//") || trimmed.startsWith("*")) return@forEachIndexed
                    val match = isPropRegex.find(line) ?: return@forEachIndexed
                    val propName = match.groupValues[1]

                    // Look at the preceding 4 lines for PropertyName("propName"). Match any of
                    // @PropertyName, @field:PropertyName, @get:PropertyName, @set:PropertyName.
                    val expectedAnnotation = "PropertyName(\"$propName\")"
                    val previousNonBlank = (idx - 1 downTo maxOf(0, idx - 4))
                        .map { lines[it] }
                    val hasAnnotation = previousNonBlank.any { it.contains(expectedAnnotation) }
                    if (!hasAnnotation) {
                        violations += "${file.path}:${idx + 1} — `$propName` is missing $expectedAnnotation"
                    }
                }
            }

        if (violations.isNotEmpty()) {
            error(
                "Found ${violations.size} `is`-prefixed Boolean field(s) without " +
                    "@PropertyName annotation:\n" + violations.joinToString("\n")
            )
        }
    }
}
