package com.jerboa.feat

import android.os.Parcelable
import androidx.annotation.StringRes
import com.jerboa.R
import com.jerboa.datatypes.types.PostView
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BlurNsfwTypes(@StringRes val resId: Int) : Parcelable {
    Nothing(R.string.app_settings_nothing),
    All(R.string.account_settings_all),
    OnlyFromNsfwCommunity(R.string.app_settings_blur_only_from_nsfw_community),
    OnlyFromSfwCommunity(R.string.app_settings_blur_only_from_sfw_community),
}

fun BlurNsfwTypes.needNsfwBlur(postView: PostView) =
    this.needNsfwBlur(postView.community.nsfw, postView.post.nsfw)

fun BlurNsfwTypes.needNsfwBlur(isCommunityNsfw: Boolean, isPostNsfw: Boolean = isCommunityNsfw): Boolean {
    return isPostNsfw && when (this) {
        BlurNsfwTypes.Nothing -> false
        BlurNsfwTypes.All -> true
        BlurNsfwTypes.OnlyFromNsfwCommunity -> isCommunityNsfw
        BlurNsfwTypes.OnlyFromSfwCommunity -> !isCommunityNsfw
    }
}
