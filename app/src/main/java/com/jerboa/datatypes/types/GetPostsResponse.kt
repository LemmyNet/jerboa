package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class GetPostsResponse(
    val posts: List<PostView>,
    val next_page: PaginationCursor? = null,
) : Parcelable
