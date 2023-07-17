package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResolvePrivateMessageReport(
    val report_id: PrivateMessageReportId,
    val resolved: Boolean,
    val auth: String,
) : Parcelable
