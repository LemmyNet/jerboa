package com.jerboa.datatypes.types

data class ModlogListParams(
    var community_id: CommunityId? = null,
    var mod_person_id: PersonId? = null,
    var other_person_id: PersonId? = null,
    var page: Int? = null,
    var limit: Int? = null,
    var hide_modlog_names: Boolean,
)