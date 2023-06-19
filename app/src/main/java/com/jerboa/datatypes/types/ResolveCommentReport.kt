package com.jerboa.datatypes.types

data class ResolveCommentReport(
    val report_id: CommentReportId,
    val resolved: Boolean,
    val auth: String,
)
