package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateMessageReportView(
    val private_message_report: PrivateMessageReport,
    val private_message: PrivateMessage,
    val private_message_creator: Person,
    val creator: Person,
    val resolver: Person? = null,
) : Parcelable
