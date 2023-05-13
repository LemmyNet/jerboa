package com.jerboa.datatypes.types

data class DeleteCustomEmojiResponse(
    var id: CustomEmojiId,
    var success: Boolean,
)