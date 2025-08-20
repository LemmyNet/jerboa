package com.jerboa.feat

import androidx.annotation.StringRes
import com.jerboa.R

enum class PostActionBarMode(
    @param:StringRes val resId: Int,
) {
    RightHandShort(R.string.post_actionbar_mode_short_right),
    LeftHandShort(R.string.post_actionbar_mode_short_left),
    Long(R.string.post_actionbar_mode_long),
}
