package com.jerboa.datatypes.types

object TestPersonObjects {
    val TEST_PERSON = Person(
        id = 1,
        name = "Lemmy Kilmister",
        display_name = "the_real_lemmy",
        avatar = "my_avatar.jpg",
        banned = false,
        published = "yes",
        updated = "yes",
        actor_id = "1",
        bio = "King of rockin'roll",
        local = false,
        banner = "https://gitarre.banner.jpg",
        deleted = false,
        matrix_user_id = "1337",
        admin = false,
        bot_account = false,
        ban_expires = null,
        instance_id = 20,

    )
    val TEST_PERSON_AGGREGATES = PersonAggregates(
        id = 1,
        person_id = TEST_PERSON.id,
        post_count = 2,
        post_score = 22,
        comment_count = 15,
        comment_score = 150,
    )
    val TEST_PERSON_VIEW = PersonView(
        person = TEST_PERSON,
        counts = TEST_PERSON_AGGREGATES,
    )
}
