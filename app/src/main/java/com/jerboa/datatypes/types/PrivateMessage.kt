package com.jerboa.datatypes.types

data class PrivateMessage(
    var id: PrivateMessageId,
    var creator_id: PersonId,
    var recipient_id: PersonId,
    var content: String,
    var deleted: Boolean,
    var read: Boolean,
    var published: String,
    var updated: String? = null,
    var ap_id: String,
    var local: Boolean,
)