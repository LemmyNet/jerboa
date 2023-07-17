package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUnreadCountResponse(
    val replies: Int,
    val mentions: Int,
    val private_messages: Int,
) : Parcelable
