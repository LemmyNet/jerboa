package com.jerboa.datatypes.types

data class AdminPurgePersonView(
    var admin_purge_person: AdminPurgePerson,
    var admin: Person? = null,
)