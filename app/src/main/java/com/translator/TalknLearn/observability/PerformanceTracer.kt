package com.translator.TalknLearn.observability

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Item 50 (docs/APP_SUGGESTIONS.md §9): thin wrapper around
 * [FirebasePerformance] custom traces so ViewModels can bracket
 * latency-sensitive flows (first translation, quiz-ready, learning sheet
 * generation) without duplicating boilerplate.
 *
 * All SDK calls are wrapped in [runCatching] so unit tests where
 * Firebase isn't initialised silently no-op (we still return a
 * [TraceHandle] so caller code is uniform).
 *
 * Trace names are validated by Firebase: ASCII letters, digits and
 * underscores, max 100 chars. Keep [TraceName] entries inside that limit.
 */
@Singleton
open class PerformanceTracer @Inject constructor() {

    /**
     * Start a trace by [name]. Always returns a non-null handle —
     * if Firebase Performance fails to start the trace (e.g. in a
     * unit test), the returned handle is a no-op.
     */
    open fun start(name: String): TraceHandle {
        val trace = runCatching { FirebasePerformance.getInstance().newTrace(name) }.getOrNull()
        runCatching { trace?.start() }
        return RealTraceHandle(trace)
    }

    interface TraceHandle {
        fun putAttribute(name: String, value: String)
        fun stop()
    }

    private class RealTraceHandle(private val trace: Trace?) : TraceHandle {
        private var stopped = false
        override fun putAttribute(name: String, value: String) {
            runCatching { trace?.putAttribute(name, value) }
        }
        override fun stop() {
            if (stopped) return
            stopped = true
            runCatching { trace?.stop() }
        }
    }

    /** Canonical trace names. */
    object TraceName {
        const val TIME_TO_FIRST_TRANSLATION = "time_to_first_translation"
        const val TIME_TO_QUIZ_READY = "time_to_quiz_ready"
        const val LEARNING_SHEET_GENERATION = "learning_sheet_generation"
    }
}
