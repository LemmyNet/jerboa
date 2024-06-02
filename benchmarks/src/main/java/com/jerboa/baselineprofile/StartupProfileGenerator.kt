package com.jerboa.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jerboa.actions.closeChangeLogIfOpen
import com.jerboa.actions.waitUntilPostsActuallyVisible
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() =
        baselineProfileRule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            includeInStartupProfile = true,
        ) {
            pressHome()
            startActivityAndWait()
            closeChangeLogIfOpen()
            waitUntilPostsActuallyVisible()
        }
}
