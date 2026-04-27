package com.translator.TalknLearn.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class BackupRulesGuardTest {

    private data class Exclude(val domain: String, val path: String)

    private val requiredExcludes = setOf(
        Exclude("sharedpref", "secure_prefs.xml"),
        Exclude("file", "datastore/translation_cache.preferences_pb"),
        Exclude("file", "datastore/language_detection_cache.preferences_pb"),
        Exclude("file", "datastore/word_bank_cache.preferences_pb"),
        Exclude("file", "datastore/seen_items.preferences_pb"),
        Exclude("file", "http_cache"),
    )

    @Test
    fun `full backup rules exclude secure prefs and local caches`() {
        val excludes = parseExcludes(File("src/main/res/xml/backup_rules.xml"))

        assertTrue(excludes.containsAll(requiredExcludes))
    }

    @Test
    fun `data extraction rules exclude secure prefs and local caches for cloud and transfer`() {
        val xml = parseXml(File("src/main/res/xml/data_extraction_rules.xml"))
        val cloudBackup = excludesUnder(xml.documentElement, "cloud-backup")
        val deviceTransfer = excludesUnder(xml.documentElement, "device-transfer")

        assertEquals(requiredExcludes, cloudBackup)
        assertEquals(requiredExcludes, deviceTransfer)
    }

    private fun parseExcludes(file: File): Set<Exclude> =
        excludesFromElement(parseXml(file).documentElement)

    private fun parseXml(file: File) =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

    private fun excludesUnder(root: Element, tagName: String): Set<Exclude> {
        val nodes = root.getElementsByTagName(tagName)
        assertEquals("Expected exactly one <$tagName> section", 1, nodes.length)
        return excludesFromElement(nodes.item(0) as Element)
    }

    private fun excludesFromElement(element: Element): Set<Exclude> {
        val nodes = element.getElementsByTagName("exclude")
        return (0 until nodes.length).map { index ->
            val exclude = nodes.item(index) as Element
            Exclude(
                domain = exclude.getAttribute("domain"),
                path = exclude.getAttribute("path")
            )
        }.toSet()
    }
}
