package com.jerboa.actions

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.jerboa.findOrFail
import com.jerboa.findOrFailTimeout
import com.jerboa.scrollThrough
import com.jerboa.scrollThroughShort

fun MacrobenchmarkScope.closeChangeLogIfOpen() {
    device.findObject(By.res("jerboa:changelogbtn"))?.click()
    device.waitForIdle()
}

fun MacrobenchmarkScope.scrollThroughPosts() {
    val feed = device.findOrFail("jerboa:posts", "Posts not found")
    feed.scrollThrough()
}

fun MacrobenchmarkScope.scrollThroughPostsShort() {
    val feed = device.findOrFail("jerboa:posts", "Posts not found")
    feed.scrollThroughShort()
}

fun MacrobenchmarkScope.scrollThroughPostsOnce() {
    val feed = device.findOrFailTimeout("jerboa:posts", "Posts not found", 60_000)
    feed.fling(Direction.DOWN)
}

fun MacrobenchmarkScope.openPost(): Boolean {
    val feed = device.findOrFail("jerboa:posts")
    val post = feed.findOrFail("jerboa:posttitle", "Post not found")
    post.click()
    device.wait(Until.gone(By.res("jerboa:loading")), 15000)

    // Returns if it succeeded at fully loading a post
    return !device.hasObject(By.res("jerboa:loading")) &&
        device.hasObject(By.res("jerboa:posttitle")) &&
        UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments")).exists() &&
        UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments")).childCount > 0
}

fun MacrobenchmarkScope.closePost() {
    device.findOrFail("jerboa:back").click()
}

fun MacrobenchmarkScope.scrollThroughComments() {
    val post = device.findOrFail("jerboa:posttitle", "Post not found")
    post.fling(Direction.UP)
    device.waitForIdle()
    val comments = UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments"))

    repeat(5) { comments.scrollToEnd(100, 10) }
    repeat(2) { comments.scrollToBeginning(100, 10) }
    comments.scrollBackward() // Makes back btn visible
}

fun MacrobenchmarkScope.doTypicalUserJourney(repeat: Int = 5) {
    repeat(repeat) {
        scrollThroughPostsOnce()
        device.waitForIdle()
        if (openPost()) {
            scrollThroughComments()
        }
        device.waitForIdle()
        closePost()
    }
}

fun MacrobenchmarkScope.waitUntilLoadingDone(timeout: Long = 10_000) {
    device.wait(Until.gone(By.res("jerboa:loading")), timeout)
}

fun MacrobenchmarkScope.waitUntilPostsActuallyVisible(timeout: Long = 30_000) {
    device.wait(Until.hasObject(By.res("jerboa:posts").hasDescendant(By.res("jerboa:post"))), timeout)
    while (device.findObjects(By.res("jerboa:post")).size < 2) {
        Thread.sleep(200)
    }
}
