package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostAggregates(
    val post_id: PostId,
    val comments: Int,
    val score: Int,
    val upvotes: Int,
    val downvotes: Int,
    val published: String,
) : Parcelable
