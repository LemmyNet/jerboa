package com.jerboa.datatypes.types

data class GetModlog(
    var mod_person_id: PersonId? = null,
    var community_id: CommunityId? = null,
    var page: Int? = null,
    var limit: Int? = null,
    var type_: ModlogActionType? /* "All" | "ModRemovePost" | "ModLockPost" | "ModFeaturePost" | "ModRemoveComment" | "ModRemoveCommunity" | "ModBanFromCommunity" | "ModAddCommunity" | "ModTransferCommunity" | "ModAdd" | "ModBan" | "ModHideCommunity" | "AdminPurgePerson" | "AdminPurgeCommunity" | "AdminPurgePost" | "AdminPurgeComment" */ = null,
    var other_person_id: PersonId? = null,
    var auth: String? = null,
)