package com.translator.TalknLearn.macrobenchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold/warm startup benchmark for the TalkNLearn launcher activity.
 *
 * Run on a connected device or emulator (API 29+):
 *   ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest
 *
 * This is the initial baseline for item 14 of docs/APP_SUGGESTIONS.md.
 * Additional flow-specific benchmarks (Quick Translate first-frame, Friends
 * list scroll jank) can be added as separate @Test methods or files.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCold() = startup(StartupMode.COLD)

    @Test
    fun startupWarm() = startup(StartupMode.WARM)

    private fun startup(mode: StartupMode) = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = DEFAULT_ITERATIONS,
        startupMode = mode,
    ) {
        pressHome()
        startActivityAndWait()
    }

    private companion object {
        const val TARGET_PACKAGE = "com.translator.TalknLearn"
        const val DEFAULT_ITERATIONS = 5
    }
}
