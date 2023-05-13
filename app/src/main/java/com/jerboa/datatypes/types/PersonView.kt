package com.jerboa.datatypes.types

data class PersonView(
    var person: Person,
    var counts: PersonAggregates,
)