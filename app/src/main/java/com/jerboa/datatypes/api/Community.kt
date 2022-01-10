package com.jerboa.datatypes.api

import com.jerboa.datatypes.CommunityModeratorView
import com.jerboa.datatypes.CommunityView
import com.jerboa.datatypes.PersonViewSafe

/**
 * You can use either `id` or `name` as an id.
 *
val * To get a federated community by name, use `name@instance.tld` .
 */
data class GetCommunity(
    val id: Int,
    val name: String?,
    val auth: String?,
)

data class GetCommunityResponse(
    val community_view: CommunityView,
    val moderators: List<CommunityModeratorView>,
    val online: Int,
)

data class CreateCommunity(
    val name: String,
    val title: String,
    val description: String,
    val icon: String?,
    val banner: String?,
    val nsfw: Boolean?,
    val auth: String?,
)

data class CommunityResponse(
    val community_view: CommunityView,
)

data class ListCommunities(
    val type_: String,
    val sort: String,
    val page: Int?,
    val limit: Int?,
    val auth: String?,
)

data class ListCommunitiesResponse(
    val communities: List<CommunityView>,
)

data class BanFromCommunity(
    val community_id: Int,
    val person_id: Int,
    val ban: Boolean,
    val remove_data: Boolean,
    val reason: String?,
    val expires: Int,
    val auth: String?,
)

data class BanFromCommunityResponse(
    val person_view: PersonViewSafe,
    val banned: Boolean,
)

data class AddModToCommunity(
    val community_id: Int,
    val person_id: Int,
    val added: Boolean,
    val auth: String,
)

data class AddModToCommunityResponse(
    val moderators: List<CommunityModeratorView>,
)

/**
 * Only mods can edit a community.
 */
data class EditCommunity(
    val community_id: Int,
    val title: String,
    val description: String?,
    val icon: String?,
    val banner: String?,
    val nsfw: Boolean?,
    val auth: String?,
)

data class DeleteCommunity(
    val community_id: Int,
    val deleted: Boolean,
    val auth: String,
)

/**
 * Only admins can remove a community.
 */
data class RemoveCommunity(
    val community_id: Int,
    val removed: Boolean,
    val reason: String,
    val expires: Int,
    val auth: String?,
)

data class FollowCommunity(
    val community_id: Int,
    val follow: Boolean,
    val auth: String,
)

data class TransferCommunity(
    val community_id: Int,
    val person_id: Int,
    val auth: String,
)

data class BlockCommunity(
    val community_id: Int,
    val block: Boolean,
    val auth: String,
)

data class BlockCommunityResponse(
    val community_view: CommunityView,
    val blocked: Boolean,
)
