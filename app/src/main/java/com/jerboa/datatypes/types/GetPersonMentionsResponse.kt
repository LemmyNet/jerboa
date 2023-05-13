package com.jerboa.datatypes.types

data class GetPersonMentionsResponse(
    var mentions: Array<PersonMentionView>,
)