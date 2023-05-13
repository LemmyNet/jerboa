package com.jerboa.datatypes.types

data class LocalUserView(
    var local_user: LocalUser,
    var person: Person,
    var counts: PersonAggregates,
)