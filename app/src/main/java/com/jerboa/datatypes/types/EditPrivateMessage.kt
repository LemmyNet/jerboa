package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditPrivateMessage(
    val private_message_id: PrivateMessageId,
    val content: String,
    val auth: String,
) : Parcelable
