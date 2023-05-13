package com.jerboa.datatypes.types

data class ResolveCommentReport(
    var report_id: CommentReportId,
    var resolved: Boolean,
    var auth: String,
)