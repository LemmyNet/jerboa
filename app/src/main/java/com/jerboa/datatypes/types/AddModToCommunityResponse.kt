package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class AddModToCommunityResponse(
    val moderators: List<CommunityModeratorView>,
) : Parcelable
