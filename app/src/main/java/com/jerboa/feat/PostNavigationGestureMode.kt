package com.jerboa.feat

import androidx.annotation.StringRes
import com.jerboa.R

enum class PostNavigationGestureMode(
    @param:StringRes val resId: Int,
) {
    /**
     * Disable all navigation gestures within posts.
     */
    Disabled(R.string.look_and_feel_post_navigation_gesture_mode_disabled),

    /**
     * Enable swiping right to navigate away from a post.
     */
    SwipeRight(R.string.look_and_feel_post_navigation_gesture_mode_swipe_right),
}
