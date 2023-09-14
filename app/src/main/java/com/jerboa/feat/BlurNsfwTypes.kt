package com.jerboa.feat

import android.os.Parcelable
import androidx.annotation.StringRes
import com.jerboa.R
import com.jerboa.datatypes.types.PostView
import com.jerboa.toEnum
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BlurNsfwTypes(@StringRes val resId: Int) : Parcelable {
    Nothing(R.string.app_settings_blur_nothing),
    All(R.string.app_settings_blur_all),
    OnlyInsideNsfwCommunities(R.string.app_settings_blur_only_inside_nsfw_community),
    OnlyOutsideNsfwCommunities(R.string.app_settings_blur_only_outside_nsfw_community);
}

fun BlurNsfwTypes.needNsfwBlur(postView : PostView) =
    this.needNsfwBlur(postView.community.nsfw, postView.post.nsfw)

fun BlurNsfwTypes.needNsfwBlur(isCommunityNsfw: Boolean, isPostNsfw: Boolean = isCommunityNsfw): Boolean {
    return isPostNsfw && when(this){
        BlurNsfwTypes.Nothing -> false
        BlurNsfwTypes.All -> true
        BlurNsfwTypes.OnlyInsideNsfwCommunities -> !isCommunityNsfw
        BlurNsfwTypes.OnlyOutsideNsfwCommunities -> isCommunityNsfw
    }
}
