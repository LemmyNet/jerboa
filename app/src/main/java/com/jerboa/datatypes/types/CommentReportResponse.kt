package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentReportResponse(
    val comment_report_view: CommentReportView,
) : Parcelable
