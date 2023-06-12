package com.jerboa

import android.graphics.Point
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

fun UiObject2.scrollThrough(qDown: Int = 10, qUp: Int = 5) {
    try {
        repeat(qDown) {
            this.fling(Direction.DOWN)
        }
        repeat(qUp) {
            this.fling(Direction.UP)
        }
        // Sometimes causes it to fling the element out of bounds making it stale
    } catch (_: StaleObjectException) {}
}

fun UiObject2.scrollThroughShort() {
    this.scrollThrough(5, 2)
}

fun UiDevice.findOrFail(resId: String, failMsg: String): UiObject2 {
    return this.findObject(By.res(resId)) ?: throw IllegalStateException(failMsg)
}

fun UiDevice.findOrFail(resId: String): UiObject2 {
    return this.findOrFail(resId, "$resId not found")
}

fun UiObject2.findOrFail(resId: String, failMsg: String): UiObject2 {
    return this.findObject(By.res(resId)) ?: throw IllegalStateException(failMsg)
}

fun UiDevice.findOrFailTimeout(resId: String, failMsg: String, timeout: Long = 5000): UiObject2 {
    val x = wait(Until.findObject(By.res(resId)), timeout)
    if (x == null) { throw IllegalStateException(failMsg) }
    return x
}
