package com.jerboa.actions

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.jerboa.findOrFail
import com.jerboa.findOrFailTimeout
import com.jerboa.findTimeout
import com.jerboa.retryOnStale
import com.jerboa.scrollThrough
import com.jerboa.scrollThroughShort
import kotlin.random.Random

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
    feed.setGestureMargin(device.displayWidth / 5)
    feed.fling(Direction.DOWN)
}

fun MacrobenchmarkScope.openPost(): Boolean {
    val post = device.findTimeout("jerboa:posttitle", 2_000)
    if (post == null) {
        scrollThroughPostsOnce()
        return openPost()
    }

    device.retryOnStale(post, "jerboa:posttitle") {
        it.click()
    }
    waitUntilLoadingDone(15_000)

    // Returns if it succeeded at fully loading a post
    return !device.hasObject(By.res("jerboa:loading")) &&
        device.hasObject(By.res("jerboa:posttitle")) &&
        UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments")).exists() &&
        UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments")).childCount > 0
}

fun MacrobenchmarkScope.closePost() {
    device.pressBack()
}

fun MacrobenchmarkScope.scrollThroughComments() {
    val post = device.findOrFailTimeout("jerboa:posttitle", "Post not found", 30_000)

    device.retryOnStale(post, "jerboa:posttitle") {
        it.scroll(Direction.DOWN, 100F)
    }
    val comments = UiScrollable(UiSelector().scrollable(true).resourceId("jerboa:comments"))

    repeat(5) { comments.flingForward() }
    repeat(2) { comments.flingBackward() }
}

fun MacrobenchmarkScope.doTypicalUserJourney(repeat: Int = 5) {
    waitUntilPostsActuallyVisible()
    setPostViewMode(postViewModes[Random.nextInt(0, 3)])
    repeat(repeat) {
        waitUntilPostsActuallyVisible()
        scrollThroughPostsOnce()
        if (openPost()) {
            // Test is flaky
            try {
                scrollThroughComments()
            } catch (_: Exception) {
            }
        }
        closePost()
    }
}

fun MacrobenchmarkScope.waitUntilLoadingDone(timeout: Long = 10_000) {
    device.wait(Until.gone(By.res("jerboa:loading")), timeout)
}

fun MacrobenchmarkScope.waitUntilPostsActuallyVisible(
    retry: Boolean = true,
    timeout: Long = 10_000,
    depth: Int = 0,
) {
    if (depth > 10) throw IllegalStateException("Exceed retrial")
    device.wait(
        Until.hasObject(By.res("jerboa:posts").hasDescendant(By.res("jerboa:post"))),
        timeout,
    )

    if (retry && !device.hasObject(By.res("jerboa:posts").hasDescendant(By.res("jerboa:post")))) {
        openMoreOptions()
        clickRefresh()
        waitUntilPostsActuallyVisible(timeout = timeout, depth = depth + 1)
    }
}

fun MacrobenchmarkScope.openMoreOptions() {
    var options = device.findTimeout("jerboa:options", timeout = 2_000)

    if (options == null) {
        val feed = device.findOrFailTimeout("jerboa:posts", "Posts not found", 2_000)
        feed.setGestureMargin(device.displayWidth / 5)
        feed.fling(Direction.UP)
        feed.fling(Direction.UP)
        options = device.findOrFailTimeout("jerboa:options")
    }

    options.click()
}

fun MacrobenchmarkScope.clickRefresh() {
    device.findOrFailTimeout("jerboa:refresh", "Refresh not found", 2_000).click()
}

fun MacrobenchmarkScope.openSortOptions() {
    device.findOrFailTimeout("jerboa:sortoptions").click()
}

fun MacrobenchmarkScope.clickMostComments() {
    device.findOrFailTimeout("jerboa:sortoption_mostcomments").click()
}

val postViewModes = listOf("SmallCard", "Card", "List")

fun MacrobenchmarkScope.setPostViewMode(postViewMode: String) {
    openMoreOptions()
    device.findOrFailTimeout("jerboa:postviewmode").click()
    device.findOrFailTimeout("jerboa:postviewmode_$postViewMode").click()
}
