package com.jerboa.feat

import com.jerboa.R

enum class PostNavigationGestureMode(val mode: Int) {
    /**
     * Disable all navigation gestures within posts.
     */
    Disabled(R.string.look_and_feel_post_navigation_gesture_mode_disabled),

    /**
     * Enable swiping left to navigate away from a post.
     */
    SwipeLeft(R.string.look_and_feel_post_navigation_gesture_mode_swipe_left),
}
