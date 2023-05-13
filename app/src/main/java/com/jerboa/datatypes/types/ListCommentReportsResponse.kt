package com.jerboa.datatypes.types

data class ListCommentReportsResponse(
    var comment_reports: Array<CommentReportView>,
)