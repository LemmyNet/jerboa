package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonMentionResponse(
    val person_mention_view: PersonMentionView,
) : Parcelable
