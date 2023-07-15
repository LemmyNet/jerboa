package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateMessageReportResponse(
    val private_message_report_view: PrivateMessageReportView,
) : Parcelable
