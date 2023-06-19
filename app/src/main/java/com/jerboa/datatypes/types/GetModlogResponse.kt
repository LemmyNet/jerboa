package com.jerboa.datatypes.types

data class GetModlogResponse(
    val removed_posts: List<ModRemovePostView>,
    val locked_posts: List<ModLockPostView>,
    val featured_posts: List<ModFeaturePostView>,
    val removed_comments: List<ModRemoveCommentView>,
    val removed_communities: List<ModRemoveCommunityView>,
    val banned_from_community: List<ModBanFromCommunityView>,
    val banned: List<ModBanView>,
    val added_to_community: List<ModAddCommunityView>,
    val transferred_to_community: List<ModTransferCommunityView>,
    val added: List<ModAddView>,
    val admin_purged_persons: List<AdminPurgePersonView>,
    val admin_purged_communities: List<AdminPurgeCommunityView>,
    val admin_purged_posts: List<AdminPurgePostView>,
    val admin_purged_comments: List<AdminPurgeCommentView>,
    val hidden_communities: List<ModHideCommunityView>,
)
