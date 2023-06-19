package com.jerboa.datatypes.types

data class Community(
    val id: CommunityId,
    val name: String,
    val title: String,
    val description: String? = null,
    val removed: Boolean,
    val published: String,
    val updated: String? = null,
    val deleted: Boolean,
    val nsfw: Boolean,
    val actor_id: String,
    val local: Boolean,
    val icon: String? = null,
    val banner: String? = null,
    val hidden: Boolean,
    val posting_restricted_to_mods: Boolean,
    val instance_id: InstanceId,
)
