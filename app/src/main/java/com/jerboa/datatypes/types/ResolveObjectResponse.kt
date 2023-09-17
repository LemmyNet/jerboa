package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResolveObjectResponse(
    val comment: CommentView? = null,
    val post: PostView? = null,
    val community: CommunityView? = null,
    val person: PersonView? = null,
) : Parcelable
