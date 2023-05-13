package com.jerboa.datatypes.types

data class Person(
    var id: PersonId,
    var name: String,
    var display_name: String? = null,
    var avatar: String? = null,
    var banned: Boolean,
    var published: String,
    var updated: String? = null,
    var actor_id: String,
    var bio: String? = null,
    var local: Boolean,
    var banner: String? = null,
    var deleted: Boolean,
    var matrix_user_id: String? = null,
    var admin: Boolean,
    var bot_account: Boolean,
    var ban_expires: String? = null,
    var instance_id: InstanceId,
)