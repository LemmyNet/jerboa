package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePrivateMessageReport(
    val private_message_id: PrivateMessageId,
    val reason: String,
) : Parcelable
