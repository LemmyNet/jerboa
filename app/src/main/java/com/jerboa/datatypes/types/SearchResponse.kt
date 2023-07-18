package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SearchResponse(
    val type_: SearchType /* "All" | "Comments" | "Posts" | "Communities" | "Users" | "Url" */,
    val comments: List<CommentView>,
    val posts: List<PostView>,
    val communities: List<CommunityView>,
    val users: List<PersonView>,
) : Parcelable
