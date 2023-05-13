package com.jerboa.datatypes.types

data class ResolvePostReport(
    var report_id: PostReportId,
    var resolved: Boolean,
    var auth: String,
)