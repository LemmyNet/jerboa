package com.jerboa.benchmarks

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.jerboa.actions.clickMostComments
import com.jerboa.actions.closeChangeLogIfOpen
import com.jerboa.actions.closePost
import com.jerboa.actions.openPost
import com.jerboa.actions.openSortOptions
import com.jerboa.actions.scrollThroughComments
import com.jerboa.actions.scrollThroughPostsOnce
import com.jerboa.actions.waitUntilLoadingDone
import com.jerboa.actions.waitUntilPostsActuallyVisible
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScrollCommentsBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollCommentsCompilationNone() = benchmark(CompilationMode.None())

    @Test
    fun scrollCommentsCompilationBaselineProfiles() = benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.WARM,
            iterations = 5,
            setupBlock = {
                pressHome()
                startActivityAndWait()
                closeChangeLogIfOpen()
                waitUntilLoadingDone()
                waitUntilPostsActuallyVisible()
                openSortOptions()
                clickMostComments()
                waitUntilPostsActuallyVisible()
                scrollThroughPostsOnce()
                while (!openPost()) { // Could fail at loading a post with its comments
                    closePost()
                    scrollThroughPostsOnce()
                }
            },
            measureBlock = {
                scrollThroughComments()
            },
        )
    }
}
