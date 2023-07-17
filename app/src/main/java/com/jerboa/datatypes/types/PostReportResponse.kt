package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostReportResponse(
    val post_report_view: PostReportView,
) : Parcelable
