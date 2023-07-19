package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class GetPersonMentionsResponse(
    val mentions: List<PersonMentionView>,
) : Parcelable
