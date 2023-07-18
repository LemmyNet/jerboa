package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateMessageResponse(
    val private_message_view: PrivateMessageView,
) : Parcelable
