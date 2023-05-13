package com.jerboa.datatypes.types

data class Community(
    var id: CommunityId,
    var name: String,
    var title: String,
    var description: String? = null,
    var removed: Boolean,
    var published: String,
    var updated: String? = null,
    var deleted: Boolean,
    var nsfw: Boolean,
    var actor_id: String,
    var local: Boolean,
    var icon: String? = null,
    var banner: String? = null,
    var hidden: Boolean,
    var posting_restricted_to_mods: Boolean,
    var instance_id: InstanceId,
)