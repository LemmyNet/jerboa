package com.jerboa.datatypes.types

data class GetModlogResponse(
    var removed_posts: Array<ModRemovePostView>,
    var locked_posts: Array<ModLockPostView>,
    var featured_posts: Array<ModFeaturePostView>,
    var removed_comments: Array<ModRemoveCommentView>,
    var removed_communities: Array<ModRemoveCommunityView>,
    var banned_from_community: Array<ModBanFromCommunityView>,
    var banned: Array<ModBanView>,
    var added_to_community: Array<ModAddCommunityView>,
    var transferred_to_community: Array<ModTransferCommunityView>,
    var added: Array<ModAddView>,
    var admin_purged_persons: Array<AdminPurgePersonView>,
    var admin_purged_communities: Array<AdminPurgeCommunityView>,
    var admin_purged_posts: Array<AdminPurgePostView>,
    var admin_purged_comments: Array<AdminPurgeCommentView>,
    var hidden_communities: Array<ModHideCommunityView>,
)