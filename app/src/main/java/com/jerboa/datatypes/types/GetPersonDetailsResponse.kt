package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class GetPersonDetailsResponse(
    val person_view: PersonView,
    val comments: List<CommentView>,
    val posts: List<PostView>,
    val moderates: List<CommunityModeratorView>,
) : Parcelable
