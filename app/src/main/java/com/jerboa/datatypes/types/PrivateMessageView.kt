package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateMessageView(
    val private_message: PrivateMessage,
    val creator: Person,
    val recipient: Person,
) : Parcelable
