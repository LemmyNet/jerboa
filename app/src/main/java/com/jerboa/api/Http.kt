package com.jerboa.api

import android.content.Context
import android.util.Log
import com.jerboa.VoteType
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.*
import com.jerboa.db.Account
import com.jerboa.newVote
import com.jerboa.serializeToMap
import com.jerboa.toastException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

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
     * Get / fetch a post.
     */
    @GET("post")
    suspend fun getPost(@QueryMap form: Map<String, String>): GetPostResponse

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

    /**
     * Like / vote on a comment.
     */
    @POST("comment/like")
    suspend fun likeComment(@Body form: CreateCommentLike): CommentResponse

    /**
     * Create a comment.
     */
    @POST("comment")
    suspend fun createComment(@Body form: CreateComment): CommentResponse

    /**
     * Save a post.
     */
    @PUT("post/save")
    suspend fun savePost(@Body form: SavePost): PostResponse

    /**
     * Save a comment.
     */
    @PUT("comment/save")
    suspend fun saveComment(@Body form: SaveComment): CommentResponse

    /**
     * Get / fetch a community.
     */
    @GET("community")
    suspend fun getCommunity(@QueryMap form: Map<String, String>): GetCommunityResponse

    /**
     * Get the details for a person.
     */
    @GET("user")
    suspend fun getPersonDetails(@QueryMap form: Map<String, String>): GetPersonDetailsResponse

    /**
     * Get comment replies.
     */
    @GET("user/replies")
    suspend fun getReplies(@QueryMap form: Map<String, String>): GetRepliesResponse

    /**
     * Mark a comment as read.
     */
    @POST("comment/mark_as_read")
    suspend fun markCommentAsRead(@Body form: MarkCommentAsRead): CommentResponse

    /**
     * Mark all replies as read.
     */
    @POST("user/mark_all_as_read")
    suspend fun markAllAsRead(@Body form: MarkAllAsRead): GetRepliesResponse

    companion object {
        private var api: API? = null
        var currentInstance: String = DEFAULT_INSTANCE
            private set

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

suspend fun getSiteWrapper(auth: String?): GetSiteResponse {
    var siteRes: GetSiteResponse? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = GetSite(auth = auth)
        Log.d(
            "jerboa",
            "Fetching site..."
        )
        siteRes = api.getSite(form = form.serializeToMap())
    } catch (e: Exception) {
        Log.e(
            "jerboa",
            e.toString()
        )
    }
    return siteRes!!
}

suspend fun likePostWrapper(
    pv: PostView,
    voteType: VoteType,
    account: Account,
    ctx: Context,
): PostResponse {
    var updatedPost: PostResponse? = null
    val api = API.getInstance()
    try {
        val newVote = newVote(currentVote = pv.my_vote, voteType = voteType)
        val form = CreatePostLike(
            post_id = pv.post.id, score = newVote, auth = account.jwt
        )
        updatedPost = api.likePost(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPost!!
}

suspend fun likeCommentWrapper(
    cv: CommentView,
    voteType: VoteType,
    account: Account,
    ctx: Context,
): CommentResponse {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val newVote = newVote(currentVote = cv.my_vote, voteType = voteType)
        val form = CreateCommentLike(
            comment_id = cv.comment.id, score = newVote, auth = account.jwt
        )
        updatedComment = api.likeComment(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedComment!!
}

suspend fun savePostWrapper(
    pv: PostView,
    account: Account,
    ctx: Context,
): PostResponse {
    var updatedPost: PostResponse? = null
    val api = API.getInstance()
    try {
        val form = SavePost(
            post_id = pv.post.id, save = !pv.saved, auth = account.jwt
        )
        updatedPost = api.savePost(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPost!!
}

suspend fun saveCommentWrapper(
    cv: CommentView,
    account: Account,
    ctx: Context,
): CommentResponse {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val form = SaveComment(
            comment_id = cv.comment.id, save = !cv.saved, auth = account.jwt
        )
        updatedComment = api.saveComment(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedComment!!
}

suspend fun markCommentAsReadWrapper(
    cv: CommentView,
    account: Account,
    ctx: Context,
): CommentResponse {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val form = MarkCommentAsRead(
            comment_id = cv.comment.id, read = !cv.comment.read, auth = account.jwt
        )
        updatedComment = api.markCommentAsRead(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedComment!!
}

suspend fun createCommentWrapper(
    form: CreateComment,
    ctx: Context,
): CommentResponse {

    var createdComment: CommentResponse? = null
    val api = API.getInstance()

    try {
        createdComment = api.createComment(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdComment!!
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
//
//
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
