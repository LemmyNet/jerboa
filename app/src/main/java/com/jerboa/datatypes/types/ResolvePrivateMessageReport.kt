package com.jerboa.datatypes.types

data class ResolvePrivateMessageReport(
    var report_id: PrivateMessageReportId,
    var resolved: Boolean,
    var auth: String,
)