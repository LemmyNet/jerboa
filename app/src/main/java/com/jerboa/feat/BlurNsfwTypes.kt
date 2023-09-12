package com.jerboa.feat

import android.os.Parcelable
import androidx.annotation.StringRes
import com.jerboa.R
import com.jerboa.datatypes.types.PostView
import com.jerboa.toEnum
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BlurNsfwTypes(@StringRes val resId: Int) : Parcelable {
    DoNotBlur(R.string.app_settings_do_not_blur),
    BlurEverywhere(R.string.app_settings_blur_everywhere),
    BlurEverywhereExceptNsfw(R.string.app_settings_blur_everywhere_except_nsfw),
    BlurOnlyNsfwCommunity(R.string.app_settings_blur_only_nsfw_community);
}

fun Int.needNsfwBlur(postView : PostView) =
    this.needNsfwBlur(postView.community.nsfw, postView.post.nsfw)

fun Int.needNsfwBlur(isCommunityNsfw: Boolean, isPostNsfw: Boolean = isCommunityNsfw): Boolean {
    return isPostNsfw && when(this.toEnum<BlurNsfwTypes>()){
        BlurNsfwTypes.DoNotBlur -> false
        BlurNsfwTypes.BlurEverywhere -> true
        BlurNsfwTypes.BlurEverywhereExceptNsfw -> !isCommunityNsfw
        BlurNsfwTypes.BlurOnlyNsfwCommunity -> isCommunityNsfw
    }
}
