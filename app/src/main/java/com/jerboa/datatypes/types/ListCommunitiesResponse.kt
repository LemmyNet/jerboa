package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ListCommunitiesResponse(
    val communities: List<CommunityView>,
) : Parcelable
