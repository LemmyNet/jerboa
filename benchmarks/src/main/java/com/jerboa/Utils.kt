package com.jerboa

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

fun UiObject2.scrollThrough(
    qDown: Int = 10,
    qUp: Int = 5,
) {
    try {
        repeat(qDown) {
            this.fling(Direction.DOWN)
        }
        repeat(qUp) {
            this.fling(Direction.UP)
        }
        // Sometimes the element becomes stale, almost guaranteed when a upfling causes a refresh
    } catch (_: StaleObjectException) {
    }
}

fun UiObject2.scrollThroughShort() {
    this.scrollThrough(5, 2)
}

fun UiDevice.findOrFail(
    resId: String,
    failMsg: String,
): UiObject2 = this.findObject(By.res(resId)) ?: throw IllegalStateException(failMsg)

fun UiDevice.findOrFail(resId: String): UiObject2 = this.findOrFail(resId, "$resId not found")

fun UiObject2.findOrFail(
    resId: String,
    failMsg: String,
): UiObject2 = this.findObject(By.res(resId)) ?: throw IllegalStateException(failMsg)

fun UiDevice.findOrFailTimeout(resId: String): UiObject2 = this.findOrFailTimeout(resId, "$resId not found")

fun UiDevice.findOrFailTimeout(
    resId: String,
    failMsg: String,
    timeout: Long = 5000,
): UiObject2 = findTimeout(resId, timeout) ?: throw IllegalStateException(failMsg)

fun UiDevice.findTimeout(
    resId: String,
    timeout: Long = 5000,
): UiObject2? = wait(Until.findObject(By.res(resId)), timeout)

// Somehow you can have device.findObject().click() be instantly Stale
// This is an attempt at solving that
fun UiDevice.retryOnStale(
    element: UiObject2,
    resId: String,
    self: (UiObject2) -> Unit,
) {
    try {
        self(element)
    } catch (_: StaleObjectException) {
        this.retryOnStale(this.findOrFailTimeout(resId), resId, self)
    }
}
