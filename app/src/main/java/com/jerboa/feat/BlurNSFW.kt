package com.jerboa.feat

import androidx.annotation.StringRes
import com.jerboa.R
import it.vercruysse.lemmyapi.datatypes.PostView

enum class BlurNSFW(
    @param:StringRes val resId: Int,
) {
    Nothing(R.string.app_settings_nothing),
    NSFW(R.string.app_settings_blur_nsfw),
    NsfwExceptFromNsfwCommunities(R.string.app_settings_blur_nsfw_except_from_nsfw_communities),
}

fun BlurNSFW.changeBlurTypeInsideCommunity() =
    if (this == BlurNSFW.NsfwExceptFromNsfwCommunities) {
        BlurNSFW.Nothing
    } else {
        this
    }

fun BlurNSFW.needBlur(postView: PostView) = this.needBlur(postView.community.nsfw, postView.post.nsfw)

fun BlurNSFW.needBlur(
    isCommunityNsfw: Boolean,
    isPostNsfw: Boolean = isCommunityNsfw,
): Boolean =
    when (this) {
        BlurNSFW.Nothing -> false
        BlurNSFW.NSFW, BlurNSFW.NsfwExceptFromNsfwCommunities -> isPostNsfw
    }
