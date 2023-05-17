package com.jerboa.datatypes.types

data class Person(
    val id: PersonId,
    val name: String,
    val display_name: String? = null,
    val avatar: String? = null,
    val banned: Boolean,
    val published: String,
    val updated: String? = null,
    val actor_id: String,
    val bio: String? = null,
    val local: Boolean,
    val banner: String? = null,
    val deleted: Boolean,
    val matrix_user_id: String? = null,
    val admin: Boolean,
    val bot_account: Boolean,
    val ban_expires: String? = null,
    val instance_id: InstanceId,
)
