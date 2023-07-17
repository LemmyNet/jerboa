package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class CommunityResponse(
    val community_view: CommunityView,
    val discussion_languages: List<LanguageId>,
) : Parcelable
