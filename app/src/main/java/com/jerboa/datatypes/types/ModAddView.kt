package com.jerboa.datatypes.types

data class ModAddView(
    var mod_add: ModAdd,
    var moderator: Person? = null,
    var modded_person: Person,
)