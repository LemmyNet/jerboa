package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ListCommentReportsResponse(
    val comment_reports: List<CommentReportView>,
) : Parcelable
