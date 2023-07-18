package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostAggregates(
    val id: Int,
    val post_id: PostId,
    val comments: Int,
    val score: Int,
    val upvotes: Int,
    val downvotes: Int,
    val published: String,
    val newest_comment_time_necro: String,
    val newest_comment_time: String,
    val featured_community: Boolean,
    val featured_local: Boolean,
    val hot_rank: Int,
    val hot_rank_active: Int,
) : Parcelable
