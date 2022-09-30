package com.jerboa.datatypes.api

import com.jerboa.datatypes.*

/**
 * Search lemmy for different types of data.
 */
data class Search(
    val q: String,
    val type_: String? = null,
    val community_id: Int? = null,
    val community_name: String? = null,
    val creator_id: Int? = null,
    val sort: String? = null,
    val listing_type: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String? = null
)

data class SearchResponse(
    val type_: String,
    val comments: List<CommentView>,
    val posts: List<PostView>,
    val communities: List<CommunityView>,
    val users: List<PersonViewSafe>
)

data class GetModlog(
    val mod_person_id: Int,
    val community_id: Int?,
    val page: Int?,
    val limit: Int?,
    val auth: String?
)

data class GetModlogResponse(
    val removed_posts: List<ModRemovePostView>,
    val locked_posts: List<ModLockPostView>,
    val stickied_posts: List<ModStickyPostView>,
    val removed_comments: List<ModRemoveCommentView>,
    val removed_communities: List<ModRemoveCommunityView>,
    val banned_from_community: List<ModBanFromCommunityView>,
    val banned: List<ModBanView>,
    val added_to_community: List<ModAddCommunityView>,
    val transferred_to_community: List<ModTransferCommunityView>,
    val added: List<ModAddView>
)

data class CreateSite(
    val name: String,
    val sidebar: String,
    val description: String?,
    val icon: String?,
    val banner: String?,
    val enable_downvotes: Boolean?,
    val open_registration: Boolean?,
    val enable_nsfw: Boolean?,
    val community_creation_admin_only: Boolean?,
    val require_email_verification: Boolean?,
    val require_application: Boolean?,
    val application_question: String?,
    val private_instance: Boolean?,
    val auth: String?
)

data class EditSite(
    val name: String,
    val sidebar: String?,
    val description: String?,
    val icon: String?,
    val banner: String?,
    val enable_downvotes: Boolean?,
    val open_registration: Boolean?,
    val enable_nsfw: Boolean?,
    val community_creation_admin_only: Boolean?,
    val require_email_verification: Boolean?,
    val require_application: Boolean?,
    val application_question: String?,
    val private_instance: Boolean?,
    val auth: String?
)

data class GetSite(
    val auth: String?
)

data class SiteResponse(
    val site_view: SiteView
)

data class GetSiteResponse(
    val site_view: SiteView?,
    val admins: List<PersonViewSafe>,
    val online: Int,
    val version: String,
    val my_user: MyUserInfo?,
    val federated_instances: FederatedInstances?
)

/**
 * Your user info, such as blocks, follows, etc.
 */
data class MyUserInfo(
    val local_user_view: LocalUserSettingsView,
    val follows: List<CommunityFollowerView>,
    val moderates: List<CommunityModeratorView>,
    val community_blocks: List<CommunityBlockView>,
    val person_blocks: List<PersonBlockView>
)

data class TransferSite(
    val person_id: Int,
    val auth: String
)

data class GetSiteConfig(
    val auth: String
)

data class GetSiteConfigResponse(
    val config_hjson: String
)

data class SaveSiteConfig(
    val config_hjson: String,
    val auth: String
)

data class FederatedInstances(
    val linked: List<String>,
    val allowed: List<String>?,
    val blocked: List<String>?
)

data class ResolveObject(
    val q: String,
    val auth: String?
)

data class ResolveObjectResponse(
    val comment: CommentView,
    val post: PostView?,
    val community: CommunityView?,
    val person: PersonViewSafe?
)

// export interface ListRegistrationApplications {
//    /**
//     * Only shows the unread applications (IE those without an admin actor)
//     */
//    unread_only?: boolean;
//    page?: number;
//    limit?: number;
//    auth: string;
// }
//
// export interface ListRegistrationApplicationsResponse {
//    registration_applications: RegistrationApplicationView[];
// }
//
// export interface ApproveRegistrationApplication {
//    id: number;
//    approve: boolean;
//    deny_reason?: string;
//    auth: string;
// }
//
// export interface RegistrationApplicationResponse {
//    registration_application: RegistrationApplicationView;
// }
//
// export interface GetUnreadRegistrationApplicationCount {
//    auth: string;
// }
//
// export interface GetUnreadRegistrationApplicationCountResponse {
//    registration_applications: number;
// }
