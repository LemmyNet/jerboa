package com.jerboa.api

import android.content.Context
import android.util.Log
import com.jerboa.VoteType
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.*
import com.jerboa.db.Account
import com.jerboa.newVote
import com.jerboa.serializeToMap
import com.jerboa.toastException
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream

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
     * Edit a comment.
     */
    @PUT("comment")
    suspend fun editComment(@Body form: EditComment): CommentResponse

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
     * Mark a person mention as read.
     */
    @POST("user/mention/mark_as_read")
    suspend fun markPersonMentionAsRead(@Body form: MarkPersonMentionAsRead): PersonMentionResponse

    /**
     * Mark a private message as read.
     */
    @POST("private_message/mark_as_read")
    suspend fun markPrivateMessageAsRead(@Body form: MarkPrivateMessageAsRead): PrivateMessageResponse

    /**
     * Mark all replies as read.
     */
    @POST("user/mark_all_as_read")
    suspend fun markAllAsRead(@Body form: MarkAllAsRead): GetRepliesResponse

    /**
     * Get mentions for your user.
     */
    @GET("user/mention")
    suspend fun getPersonMentions(@QueryMap form: Map<String, String>): GetPersonMentionsResponse

    /**
     * Get / fetch private messages.
     */
    @GET("private_message/list")
    suspend fun getPrivateMessages(@QueryMap form: Map<String, String>): PrivateMessagesResponse

    /**
     * Create a private message.
     */
    @POST("private_message")
    suspend fun createPrivateMessage(@Body form: CreatePrivateMessage): PrivateMessageResponse

    /**
     * Get your unread counts
     */
    @GET("user/unread_count")
    suspend fun getUnreadCount(@QueryMap form: Map<String, String>): GetUnreadCountResponse

    /**
     * Follow / subscribe to a community.
     */
    @POST("community/follow")
    suspend fun followCommunity(@Body form: FollowCommunity): CommunityResponse

    /**
     * Create a post.
     */
    @POST("post")
    suspend fun createPost(@Body form: CreatePost): PostResponse

    /**
     * Edit a post.
     */
    @PUT("post")
    suspend fun editPost(@Body form: EditPost): PostResponse

    /**
     * Search lemmy.
     */
    @GET("search")
    suspend fun search(@QueryMap form: Map<String, String>): SearchResponse

    /**
     * Fetch metadata for any given site.
     */
    @GET("post/site_metadata")
    suspend fun getSiteMetadata(@QueryMap form: Map<String, String>): GetSiteMetadataResponse

    /**
     * Upload an image.
     */
    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Header("Cookie") token: String,
        @Part filePart: MultipartBody.Part
    ): PictrsImages

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

        private fun buildApi(): API {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder()
                .baseUrl(buildUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(API::class.java)
        }
    }
}

suspend fun followCommunityWrapper(
    communityView: CommunityView,
    auth: String,
): CommunityResponse? {
    var communityRes: CommunityResponse? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = FollowCommunity(
            community_id = communityView.community.id,
            follow = !communityView.subscribed,
            auth = auth
        )
        Log.d(
            "jerboa",
            "Following community ..."
        )
        communityRes = api.followCommunity(form)
    } catch (e: Exception) {
        Log.e(
            "jerboa",
            e.toString()
        )
    }
    return communityRes
}

suspend fun getSiteWrapper(auth: String?): GetSiteResponse? {
    var siteRes: GetSiteResponse? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = GetSite(auth = auth)
        Log.d(
            "jerboa",
            "Fetching site: $form ..."
        )
        siteRes = api.getSite(form = form.serializeToMap())
    } catch (e: Exception) {
        Log.e(
            "jerboa",
            e.toString()
        )
    }
    return siteRes
}

suspend fun getSiteMetadataWrapper(url: String): SiteMetadata? {
    var res: SiteMetadata? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = GetSiteMetadata(url = url)
        Log.d(
            "jerboa",
            "Fetching site metadata..."
        )
        res = api.getSiteMetadata(form = form.serializeToMap()).metadata
    } catch (e: Exception) {
        Log.e(
            "jerboa",
            e.toString()
        )
    }
    return res
}

suspend fun fetchPostsWrapper(
    account: Account?,
    ctx: Context?,
    communityId: Int? = null,
    sortType: SortType,
    listingType: ListingType,
    page: Int,

): List<PostView> {
    var posts = listOf<PostView>()
    val api = API.getInstance()

    try {
        val form = GetPosts(
            community_id = communityId,
            sort = sortType.toString(),
            type_ = listingType.toString(),
            page = page,
            auth = account?.jwt,
        )
        Log.d(
            "jerboa",
            "Fetching posts: $form"
        )
        posts = api.getPosts(form = form.serializeToMap()).posts
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }

    return posts
}

suspend fun searchWrapper(
    account: Account?,
    ctx: Context?,
    communityId: Int? = null,
    sortType: SortType,
    listingType: ListingType,
    searchType: SearchType,
    page: Int? = null,
    query: String,
    creatorId: Int? = null,
): SearchResponse? {
    var res: SearchResponse? = null
    val api = API.getInstance()

    try {
        val form = Search(
            q = query,
            type_ = searchType.toString(),
            creator_id = creatorId,
            community_id = communityId,
            sort = sortType.toString(),
            listing_type = listingType.toString(),
            page = page,
            auth = account?.jwt,
        )
        Log.d(
            "jerboa",
            "Searching: $form"
        )
        res = api.search(form = form.serializeToMap())
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }

    return res
}

suspend fun createPostWrapper(
    account: Account,
    ctx: Context?,
    communityId: Int,
    body: String?,
    url: String?,
    name: String,
): PostView? {
    var createdPostView: PostView? = null
    val api = API.getInstance()

    try {
        val form = CreatePost(
            name = name,
            community_id = communityId,
            body = body,
            url = url,
            auth = account.jwt,
        )
        Log.d(
            "jerboa",
            "Creating post: $form"
        )
        createdPostView = api.createPost(form).post_view
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdPostView
}

suspend fun editPostWrapper(
    postView: PostView,
    account: Account,
    ctx: Context?,
    body: String?,
    url: String?,
    name: String,
): PostView? {
    var editedPostView: PostView? = null
    val api = API.getInstance()

    try {
        val form = EditPost(
            post_id = postView.post.id,
            name = name,
            body = body,
            url = url,
            auth = account.jwt,
        )
        Log.d(
            "jerboa",
            "Editing post: $form"
        )
        editedPostView = api.editPost(form).post_view
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return editedPostView
}

suspend fun likePostWrapper(
    pv: PostView,
    voteType: VoteType,
    account: Account,
    ctx: Context,
): PostResponse? {
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
    return updatedPost
}

suspend fun likeCommentWrapper(
    cv: CommentView,
    voteType: VoteType,
    account: Account,
    ctx: Context,
): CommentResponse? {
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
    return updatedComment
}

suspend fun savePostWrapper(
    pv: PostView,
    account: Account,
    ctx: Context,
): PostResponse? {
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
    return updatedPost
}

suspend fun saveCommentWrapper(
    cv: CommentView,
    account: Account,
    ctx: Context,
): CommentResponse? {
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
    return updatedComment
}

suspend fun markCommentAsReadWrapper(
    cv: CommentView,
    account: Account,
    ctx: Context,
): CommentResponse? {
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
    return updatedComment
}

suspend fun markPersonMentionAsReadWrapper(
    personMentionView: PersonMentionView,
    account: Account,
    ctx: Context,
): PersonMentionResponse? {
    var updatedPm: PersonMentionResponse? = null
    val api = API.getInstance()
    try {
        val form = MarkPersonMentionAsRead(
            person_mention_id = personMentionView.person_mention.id,
            read = !personMentionView.person_mention.read,
            auth = account
                .jwt
        )
        updatedPm = api.markPersonMentionAsRead(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPm
}

suspend fun markPrivateMessageAsReadWrapper(
    pm: PrivateMessageView,
    account: Account,
    ctx: Context,
): PrivateMessageResponse? {
    var updatedPm: PrivateMessageResponse? = null
    val api = API.getInstance()
    try {
        val form = MarkPrivateMessageAsRead(
            private_message_id = pm.private_message.id, read = !pm.private_message.read,
            auth = account
                .jwt
        )
        updatedPm = api.markPrivateMessageAsRead(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPm
}

suspend fun createCommentWrapper(
    form: CreateComment,
    ctx: Context,
): CommentResponse? {

    var createdComment: CommentResponse? = null
    val api = API.getInstance()

    try {
        createdComment = api.createComment(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdComment
}

suspend fun editCommentWrapper(
    form: EditComment,
    ctx: Context,
): CommentResponse? {

    var editedComment: CommentResponse? = null
    val api = API.getInstance()

    try {
        editedComment = api.editComment(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return editedComment
}

suspend fun createPrivateMessageWrapper(
    form: CreatePrivateMessage,
    ctx: Context,
): PrivateMessageResponse? {

    var createdPrivateMessage: PrivateMessageResponse? = null
    val api = API.getInstance()

    try {
        createdPrivateMessage = api.createPrivateMessage(form)
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdPrivateMessage
}

suspend fun uploadPictrsImage(account: Account, imageIs: InputStream, ctx: Context): String? {
    var imageUrl: String? = null
    val api = API.getInstance()
    try {
        Log.d("jerboa", "Uploading image....")
        val part = MultipartBody.Part.createFormData(
            "images[]",
            "myPic",
            imageIs.readBytes().toRequestBody()
        )
        val url = "https://${API.currentInstance}/pictrs/image"
        val cookie = "jwt=${account.jwt}"
        val images = api.uploadImage(url, cookie, part)
        Log.d("jerboa", "Uploading done.")
        imageUrl = "$url/${images.files?.get(0)?.file}"
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return imageUrl
}

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
//
//
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
//
//
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
//
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
//
//  /**
//   * Add an admin to your site.
//   */
//  async addAdmin(form: AddAdmin): Promise<AddAdminResponse> {
//    return this.wrapper(HttpType.Post, "/admin/add", form);
//  }
//
