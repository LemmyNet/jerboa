package com.jerboa.feat

import androidx.annotation.StringRes
import com.jerboa.R


enum class PostActionbarMode(@StringRes val resId: Int) {
    Long(R.string.post_actionbar_mode_long),
    LeftHandShort(R.string.post_actionbar_mode_short_left),
    RightHandShort(R.string.post_actionbar_mode_short_right),
}
