package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePrivateMessage(
    val content: String,
    val recipient_id: PersonId,
) : Parcelable
