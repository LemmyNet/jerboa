package com.jerboa.datatypes.types

data class PrivateMessageReportView(
    var private_message_report: PrivateMessageReport,
    var private_message: PrivateMessage,
    var private_message_creator: Person,
    var creator: Person,
    var resolver: Person? = null,
)