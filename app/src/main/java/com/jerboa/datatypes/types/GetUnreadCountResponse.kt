package com.jerboa.datatypes.types

data class GetUnreadCountResponse(
    var replies: Int,
    var mentions: Int,
    var private_messages: Int,
)