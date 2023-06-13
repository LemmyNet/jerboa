package com.jerboa.baselineprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jerboa.actions.closeChangeLogIfOpen
import com.jerboa.actions.waitUntilPostsActuallyVisible
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupProfileGenerator {
    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun startup() =
        baselineProfileRule.collectBaselineProfile(
            packageName = "com.jerboa",
            maxIterations = 15,
            includeInStartupProfile = true,

        ) {
            pressHome()
            startActivityAndWait()
            closeChangeLogIfOpen()
            waitUntilPostsActuallyVisible()
        }
}
