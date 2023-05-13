package com.jerboa.datatypes.types

data class PersonAggregates(
    var id: Int,
    var person_id: PersonId,
    var post_count: Int,
    var post_score: Int,
    var comment_count: Int,
    var comment_score: Int,
)