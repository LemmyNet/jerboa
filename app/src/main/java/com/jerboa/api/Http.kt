package com.jerboa.api

import com.jerboa.datatypes.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

const val VERSION = "v3"
const val DEFAULT_INSTANCE = "lemmy.ml"

interface API {
    @GET("site")
    suspend fun getSite(@QueryMap form: Map<String, String>): GetSiteResponse

    /**
     * Get / fetch posts, with various filters.
     */
    @GET("post/list")
    suspend fun getPosts(@QueryMap form: Map<String, String>): GetPostsResponse

    /**
     * Log into lemmy.
     */
    @POST("user/login")
    suspend fun login(@Body form: Login): LoginResponse

    /**
     * Like / vote on a post.
     */
    @POST("post/like")
    suspend fun likePost(@Body form: CreatePostLike): PostResponse

    companion object {
        private var api: API? = null
        private var currentInstance: String = DEFAULT_INSTANCE

        private fun buildUrl(): String {
            return "https://$currentInstance/api/$VERSION/"
        }

        fun changeLemmyInstance(instance: String): API {
            currentInstance = instance
            api = buildApi()
            return api!!
        }

        fun getInstance(): API {
            if (api == null) {
                api = buildApi()
            }
            return api!!
        }

        private fun buildApi(): API? {
            return Retrofit.Builder()
                .baseUrl(buildUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(API::class.java)
        }
    }
}

//
// /**
// * Helps build lemmy HTTP requests.
// */
// export class LemmyHttp {
//  private apiUrl: string;
//  private headers: { [key: string]: string } = {};
//
//  /**
//   * Generates a new instance of LemmyHttp.
//   * @param baseUrl the base url, without the vX version: https://lemmy.ml -> goes to https://lemmy.ml/api/vX
//   * @param headers optional headers. Should contain `x-real-ip` and `x-forwarded-for` .
//   */
//  constructor(baseUrl: string, headers?: { [key: string]: string }) {
//    this.apiUrl = `${baseUrl}/api/${VERSION}`;
//
//    if (headers) {
//      this.headers = headers;
//    }
//  }
//
//
//  /**
//   * Create your site.
//   */
//  async createSite(form: CreateSite): Promise<SiteResponse> {
//    return this.wrapper(HttpType.Post, "/site", form);
//  }
//
//  /**
//   * Edit your site.
//   */
//  async editSite(form: EditSite): Promise<SiteResponse> {
//    return this.wrapper(HttpType.Put, "/site", form);
//  }
//
//  /**
//   * Transfer your site to another user.
//   */
//  async transferSite(form: TransferSite): Promise<GetSiteResponse> {
//    return this.wrapper(HttpType.Post, "/site/transfer", form);
//  }
//
//  /**
//   * Get your site configuration.
//   */
//  async getSiteConfig(form: GetSiteConfig): Promise<GetSiteConfigResponse> {
//    return this.wrapper(HttpType.Get, "/site/config", form);
//  }
//
//  /**
//   * Save your site config.
//   */
//  async saveSiteConfig(form: SaveSiteConfig): Promise<GetSiteConfigResponse> {
//    return this.wrapper(HttpType.Put, "/site/config", form);
//  }
//
//  /**
//   * Get the modlog.
//   */
//  async getModlog(form: GetModlog): Promise<GetModlogResponse> {
//    return this.wrapper(HttpType.Get, "/modlog", form);
//  }
//
//  /**
//   * Search lemmy.
//   */
//  async search(form: Search): Promise<SearchResponse> {
//    return this.wrapper(HttpType.Get, "/search", form);
//  }
//
//  /**
//   * Fetch a non-local / federated object.
//   */
//  async resolveObject(form: ResolveObject): Promise<ResolveObjectResponse> {
//    return this.wrapper(HttpType.Get, "/resolve_object", form);
//  }
//
//  /**
//   * Create a new community.
//   */
//  async createCommunity(form: CreateCommunity): Promise<CommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community", form);
//  }
//
//  /**
//   * Get / fetch a community.
//   */
//  async getCommunity(form: GetCommunity): Promise<GetCommunityResponse> {
//    return this.wrapper(HttpType.Get, "/community", form);
//  }
//
//  /**
//   * Edit a community.
//   */
//  async editCommunity(form: EditCommunity): Promise<CommunityResponse> {
//    return this.wrapper(HttpType.Put, "/community", form);
//  }
//
//  /**
//   * List communities, with various filters.
//   */
//  async listCommunities(
//  form: ListCommunities
//  ): Promise<ListCommunitiesResponse> {
//    return this.wrapper(HttpType.Get, "/community/list", form);
//  }
//
//  /**
//   * Follow / subscribe to a community.
//   */
//  async followCommunity(form: FollowCommunity): Promise<CommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/follow", form);
//  }
//
//  /**
//   * Block a community.
//   */
//  async blockCommunity(form: BlockCommunity): Promise<BlockCommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/block", form);
//  }
//
//  /**
//   * Delete a community.
//   */
//  async deleteCommunity(form: DeleteCommunity): Promise<CommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/delete", form);
//  }
//
//  /**
//   * A moderator remove for a community.
//   */
//  async removeCommunity(form: RemoveCommunity): Promise<CommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/remove", form);
//  }
//
//  /**
//   * Transfer your community to an existing moderator.
//   */
//  async transferCommunity(
//  form: TransferCommunity
//  ): Promise<GetCommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/transfer", form);
//  }
//
//  /**
//   * Ban a user from a community.
//   */
//  async banFromCommunity(
//  form: BanFromCommunity
//  ): Promise<BanFromCommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/ban_user", form);
//  }
//
//  /**
//   * Add a moderator to your community.
//   */
//  async addModToCommunity(
//  form: AddModToCommunity
//  ): Promise<AddModToCommunityResponse> {
//    return this.wrapper(HttpType.Post, "/community/mod", form);
//  }
//
//  /**
//   * Create a post.
//   */
//  async createPost(form: CreatePost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Post, "/post", form);
//  }
//
//  /**
//   * Get / fetch a post.
//   */
//  async getPost(form: GetPost): Promise<GetPostResponse> {
//    return this.wrapper(HttpType.Get, "/post", form);
//  }
//
//  /**
//   * Edit a post.
//   */
//  async editPost(form: EditPost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Put, "/post", form);
//  }
//
//  /**
//   * Delete a post.
//   */
//  async deletePost(form: DeletePost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Post, "/post/delete", form);
//  }
//
//  /**
//   * A moderator remove for a post.
//   */
//  async removePost(form: RemovePost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Post, "/post/remove", form);
//  }
//
//  /**
//   * A moderator can lock a post ( IE disable new comments ).
//   */
//  async lockPost(form: LockPost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Post, "/post/lock", form);
//  }
//
//  /**
//   * A moderator can sticky a post ( IE stick it to the top of a community ).
//   */
//  async stickyPost(form: StickyPost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Post, "/post/sticky", form);
//  }
//
//
//
//  /**
//   * Save a post.
//   */
//  async savePost(form: SavePost): Promise<PostResponse> {
//    return this.wrapper(HttpType.Put, "/post/save", form);
//  }
//
//  /**
//   * Report a post.
//   */
//  async createPostReport(form: CreatePostReport): Promise<PostReportResponse> {
//    return this.wrapper(HttpType.Post, "/post/report", form);
//  }
//
//  /**
//   * Resolve a post report. Only a mod can do this.
//   */
//  async resolvePostReport(
//  form: ResolvePostReport
//  ): Promise<PostReportResponse> {
//    return this.wrapper(HttpType.Put, "/post/report/resolve", form);
//  }
//
//  /**
//   * List post reports.
//   */
//  async listPostReports(
//  form: ListPostReports
//  ): Promise<ListPostReportsResponse> {
//    return this.wrapper(HttpType.Get, "/post/report/list", form);
//  }
//
//  /**
//   * Fetch metadata for any given site.
//   */
//  async getSiteMetadata(
//  form: GetSiteMetadata
//  ): Promise<GetSiteMetadataResponse> {
//    return this.wrapper(HttpType.Get, "/post/site_metadata", form);
//  }
//
//  /**
//   * Create a comment.
//   */
//  async createComment(form: CreateComment): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Post, "/comment", form);
//  }
//
//  /**
//   * Edit a comment.
//   */
//  async editComment(form: EditComment): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Put, "/comment", form);
//  }
//
//  /**
//   * Delete a comment.
//   */
//  async deleteComment(form: DeleteComment): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Post, "/comment/delete", form);
//  }
//
//  /**
//   * A moderator remove for a comment.
//   */
//  async removeComment(form: RemoveComment): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Post, "/comment/remove", form);
//  }
//
//  /**
//   * Mark a comment as read.
//   */
//  async markCommentAsRead(form: MarkCommentAsRead): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Post, "/comment/mark_as_read", form);
//  }
//
//  /**
//   * Like / vote on a comment.
//   */
//  async likeComment(form: CreateCommentLike): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Post, "/comment/like", form);
//  }
//
//  /**
//   * Save a comment.
//   */
//  async saveComment(form: SaveComment): Promise<CommentResponse> {
//    return this.wrapper(HttpType.Put, "/comment/save", form);
//  }
//
//  /**
//   * Get / fetch comments.
//   */
//  async getComments(form: GetComments): Promise<GetCommentsResponse> {
//    return this.wrapper(HttpType.Get, "/comment/list", form);
//  }
//
//  /**
//   * Report a comment.
//   */
//  async createCommentReport(
//  form: CreateCommentReport
//  ): Promise<CommentReportResponse> {
//    return this.wrapper(HttpType.Post, "/comment/report", form);
//  }
//
//  /**
//   * Resolve a comment report. Only a mod can do this.
//   */
//  async resolveCommentReport(
//  form: ResolveCommentReport
//  ): Promise<CommentReportResponse> {
//    return this.wrapper(HttpType.Put, "/comment/report/resolve", form);
//  }
//
//  /**
//   * List comment reports.
//   */
//  async listCommentReports(
//  form: ListCommentReports
//  ): Promise<ListCommentReportsResponse> {
//    return this.wrapper(HttpType.Get, "/comment/report/list", form);
//  }
//
//  /**
//   * Get / fetch private messages.
//   */
//  async getPrivateMessages(
//  form: GetPrivateMessages
//  ): Promise<PrivateMessagesResponse> {
//    return this.wrapper(HttpType.Get, "/private_message/list", form);
//  }
//
//  /**
//   * Create a private message.
//   */
//  async createPrivateMessage(
//  form: CreatePrivateMessage
//  ): Promise<PrivateMessageResponse> {
//    return this.wrapper(HttpType.Post, "/private_message", form);
//  }
//
//  /**
//   * Edit a private message.
//   */
//  async editPrivateMessage(
//  form: EditPrivateMessage
//  ): Promise<PrivateMessageResponse> {
//    return this.wrapper(HttpType.Put, "/private_message", form);
//  }
//
//  /**
//   * Delete a private message.
//   */
//  async deletePrivateMessage(
//  form: DeletePrivateMessage
//  ): Promise<PrivateMessageResponse> {
//    return this.wrapper(HttpType.Post, "/private_message/delete", form);
//  }
//
//  /**
//   * Mark a private message as read.
//   */
//  async markPrivateMessageAsRead(
//  form: MarkPrivateMessageAsRead
//  ): Promise<PrivateMessageResponse> {
//    return this.wrapper(HttpType.Post, "/private_message/mark_as_read", form);
//  }
//
//  /**
//   * Register a new user.
//   */
//  async register(form: Register): Promise<LoginResponse> {
//    return this.wrapper(HttpType.Post, "/user/register", form);
//  }
//
//
//  /**
//   * Get the details for a person.
//   */
//  async getPersonDetails(
//  form: GetPersonDetails
//  ): Promise<GetPersonDetailsResponse> {
//    return this.wrapper(HttpType.Get, "/user", form);
//  }
//
//  /**
//   * Get mentions for your user.
//   */
//  async getPersonMentions(
//  form: GetPersonMentions
//  ): Promise<GetPersonMentionsResponse> {
//    return this.wrapper(HttpType.Get, "/user/mention", form);
//  }
//
//  /**
//   * Mark a person mention as read.
//   */
//  async markPersonMentionAsRead(
//  form: MarkPersonMentionAsRead
//  ): Promise<PersonMentionResponse> {
//    return this.wrapper(HttpType.Post, "/user/mention/mark_as_read", form);
//  }
//
//  /**
//   * Get comment replies.
//   */
//  async getReplies(form: GetReplies): Promise<GetRepliesResponse> {
//    return this.wrapper(HttpType.Get, "/user/replies", form);
//  }
//
//  /**
//   * Ban a person from your site.
//   */
//  async banPerson(form: BanPerson): Promise<BanPersonResponse> {
//    return this.wrapper(HttpType.Post, "/user/ban", form);
//  }
//
//  /**
//   * Block a person.
//   */
//  async blockPerson(form: BlockPerson): Promise<BlockPersonResponse> {
//    return this.wrapper(HttpType.Post, "/user/block", form);
//  }
//
//  /**
//   * Fetch a Captcha.
//   */
//  async getCaptcha(): Promise<GetCaptchaResponse> {
//    return this.wrapper(HttpType.Get, "/user/get_captcha", {});
//  }
//
//  /**
//   * Delete your account.
//   */
//  async deleteAccount(form: DeleteAccount): Promise<LoginResponse> {
//    return this.wrapper(HttpType.Post, "/user/delete_account", form);
//  }
//
//  /**
//   * Reset your password.
//   */
//  async passwordReset(form: PasswordReset): Promise<PasswordResetResponse> {
//    return this.wrapper(HttpType.Post, "/user/password_reset", form);
//  }
//
//  /**
//   * Change your password from an email / token based reset.
//   */
//  async passwordChange(form: PasswordChange): Promise<LoginResponse> {
//    return this.wrapper(HttpType.Post, "/user/password_change", form);
//  }
//
//  /**
//   * Mark all replies as read.
//   */
//  async markAllAsRead(form: MarkAllAsRead): Promise<GetRepliesResponse> {
//    return this.wrapper(HttpType.Post, "/user/mark_all_as_read", form);
//  }
//
//  /**
//   * Save your user settings.
//   */
//  async saveUserSettings(form: SaveUserSettings): Promise<LoginResponse> {
//    return this.wrapper(HttpType.Put, "/user/save_user_settings", form);
//  }
//
//  /**
//   * Change your user password.
//   */
//  async changePassword(form: ChangePassword): Promise<LoginResponse> {
//    return this.wrapper(HttpType.Put, "/user/change_password", form);
//  }
//
//  /**
//   * Get counts for your reports
//   */
//  async getReportCount(form: GetReportCount): Promise<GetReportCountResponse> {
//    return this.wrapper(HttpType.Get, "/user/report_count", form);
//  }
//
//  /**
//   * Get your unread counts
//   */
//  async getUnreadCount(form: GetUnreadCount): Promise<GetUnreadCountResponse> {
//    return this.wrapper(HttpType.Get, "/user/unread_count", form);
//  }
//
//  /**
//   * Add an admin to your site.
//   */
//  async addAdmin(form: AddAdmin): Promise<AddAdminResponse> {
//    return this.wrapper(HttpType.Post, "/admin/add", form);
//  }
//
//  private async wrapper<ResponseType, MessageType>(
//  type_: HttpType,
//  endpoint: string,
//  form: MessageType
//  ): Promise<ResponseType> {
//    if (type_ == HttpType.Get) {
//      let getUrl = `${this.buildFullUrl(endpoint)}?${encodeGetParams(form)}`;
//      return fetch(getUrl, {
//        method: "GET",
//        headers: this.headers,
//      }).then(d => d.json() as Promise<ResponseType>);
//    } else {
//      return fetch(this.buildFullUrl(endpoint), {
//        method: type_,
//        headers: {
//        "Content-Type": "application/json",
//        ...this.headers,
//      },
//        body: JSON.stringify(form),
//      }).then(d => d.json() as Promise<ResponseType>);
//    }
//  }
// }
