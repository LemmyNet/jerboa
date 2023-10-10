package com.jerboa.feat

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.datatypes.types.PostView
import com.jerboa.toEnum
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BlurTypes(@StringRes val resId: Int) : Parcelable {
    Nothing(R.string.app_settings_nothing),
    NSFW(R.string.app_settings_blur_nsfw),
    NsfwExceptFromNsfwCommunities(R.string.app_settings_blur_nsfw_except_from_nsfw_communities);

    companion object {
        fun changeBlurTypeInsideCommunity(blurTypes: Int): Int =
            if (blurTypes.toEnum<BlurTypes>() == NsfwExceptFromNsfwCommunities) {
                Nothing.ordinal
            } else {
                blurTypes
            }
    }
}

fun BlurTypes.needBlur(postView: PostView) =
    this.needBlur(postView.community.nsfw, postView.post.nsfw)

fun BlurTypes.needBlur(isCommunityNsfw: Boolean, isPostNsfw: Boolean = isCommunityNsfw): Boolean {
    return when (this) {
        BlurTypes.Nothing -> false
        BlurTypes.NSFW, BlurTypes.NsfwExceptFromNsfwCommunities -> isPostNsfw
    }
}



