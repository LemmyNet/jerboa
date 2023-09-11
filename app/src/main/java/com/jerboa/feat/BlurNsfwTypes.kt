package com.jerboa.feat

import android.os.Parcelable
import androidx.annotation.StringRes
import com.jerboa.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BlurNsfwTypes(@StringRes val resId: Int) : Parcelable {
    DoNotBlur(R.string.app_settings_do_not_blur),
    BlurEverywhere(R.string.app_settings_blur_everywhere),
    BlurEverywhereExceptNsfw(R.string.app_settings_blur_everywhere_except_nsfw),
    BlurOnlyNsfwCommunity(R.string.app_settings_blur_only_nsfw_community),
}
