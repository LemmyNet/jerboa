package com.jerboa.datatypes.types

data class ModBanView(
    var mod_ban: ModBan,
    var moderator: Person? = null,
    var banned_person: Person,
)