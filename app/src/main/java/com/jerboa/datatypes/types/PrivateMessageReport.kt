package com.jerboa.datatypes.types

data class PrivateMessageReport(
    var id: PrivateMessageReportId,
    var creator_id: PersonId,
    var private_message_id: PrivateMessageId,
    var original_pm_text: String,
    var reason: String,
    var resolved: Boolean,
    var resolver_id: PersonId? = null,
    var published: String,
    var updated: String? = null,
)