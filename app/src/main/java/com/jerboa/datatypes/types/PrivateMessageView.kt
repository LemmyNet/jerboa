package com.jerboa.datatypes.types

data class PrivateMessageView(
    var private_message: PrivateMessage,
    var creator: Person,
    var recipient: Person,
)