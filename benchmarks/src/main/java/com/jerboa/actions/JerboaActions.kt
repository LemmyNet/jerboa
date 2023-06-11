package com.jerboa.actions

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By

fun MacrobenchmarkScope.closeChangeLogIfOpen(){
    val obj = device.findObject(By.res("jerboa:changelogbtn"))
    obj?.click()
    device.waitForIdle()
}


