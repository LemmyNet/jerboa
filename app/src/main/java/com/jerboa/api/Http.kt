package com.jerboa.api

import android.content.Context
import android.util.Log
import arrow.core.Either
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.*
import com.jerboa.db.Account
import com.jerboa.util.VoteType
import com.jerboa.util.newVote
import com.jerboa.util.serializeToMap
import com.jerboa.util.toastException
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream

const val VERSION = "v3"
const val DEFAULT_INSTANCE = "lemmy.ml"

interface API {
    @GET("site")
    suspend fun getSite(@QueryMap form: Map<String, String>): Response<GetSiteResponse>

    /**
     * Get / fetch posts, with various filters.
     */
    @GET("post/list")
    suspend fun getPosts(@QueryMap form: Map<String, String>): Response<GetPostsResponse>

    /**
     * Get / fetch a post.
     */
    @GET("post")
    suspend fun getPost(@QueryMap form: Map<String, String>): Response<GetPostResponse>

    /**
     * Log into lemmy.
     */
    @POST("user/login")
    suspend fun login(@Body form: Login): Response<LoginResponse>

    /**
     * Like / vote on a post.
     */
    @POST("post/like")
    suspend fun likePost(@Body form: CreatePostLike): Response<PostResponse>

    /**
     * Like / vote on a comment.
     */
    @POST("comment/like")
    suspend fun likeComment(@Body form: CreateCommentLike): Response<CommentResponse>

    /**
     * Create a comment.
     */
    @POST("comment")
    suspend fun createComment(@Body form: CreateComment): Response<CommentResponse>

    /**
     * Edit a comment.
     */
    @PUT("comment")
    suspend fun editComment(@Body form: EditComment): Response<CommentResponse>

    /**
     * Delete a comment.
     */
    @POST("comment/delete")
    suspend fun deleteComment(@Body form: DeleteComment): Response<CommentResponse>

    /**
     * Save a post.
     */
    @PUT("post/save")
    suspend fun savePost(@Body form: SavePost): Response<PostResponse>

    /**
     * Save a comment.
     */
    @PUT("comment/save")
    suspend fun saveComment(@Body form: SaveComment): Response<CommentResponse>

    /**
     * Get / fetch comments.
     */
    @GET("comment/list")
    suspend fun getComments(@QueryMap form: Map<String, String>): Response<GetCommentsResponse>

    /**
     * Get / fetch a community.
     */
    @GET("community")
    suspend fun getCommunity(@QueryMap form: Map<String, String>): Response<GetCommunityResponse>

    /**
     * Get the details for a person.
     */
    @GET("user")
    suspend fun getPersonDetails(@QueryMap form: Map<String, String>): Response<GetPersonDetailsResponse>

    /**
     * Get comment replies.
     */
    @GET("user/replies")
    suspend fun getReplies(@QueryMap form: Map<String, String>): Response<GetRepliesResponse>

    /**
     * Mark a comment as read.
     */
    @POST("comment/mark_as_read")
    suspend fun markCommentReplyAsRead(@Body form: MarkCommentReplyAsRead): Response<CommentResponse>

    /**
     * Mark a person mention as read.
     */
    @POST("user/mention/mark_as_read")
    suspend fun markPersonMentionAsRead(@Body form: MarkPersonMentionAsRead): Response<PersonMentionResponse>

    /**
     * Mark a private message as read.
     */
    @POST("private_message/mark_as_read")
    suspend fun markPrivateMessageAsRead(@Body form: MarkPrivateMessageAsRead): Response<PrivateMessageResponse>

    /**
     * Mark all replies as read.
     */
    @POST("user/mark_all_as_read")
    suspend fun markAllAsRead(@Body form: MarkAllAsRead): Response<GetRepliesResponse>

    /**
     * Get mentions for your user.
     */
    @GET("user/mention")
    suspend fun getPersonMentions(@QueryMap form: Map<String, String>): Response<GetPersonMentionsResponse>

    /**
     * Get / fetch private messages.
     */
    @GET("private_message/list")
    suspend fun getPrivateMessages(@QueryMap form: Map<String, String>): Response<PrivateMessagesResponse>

    /**
     * Create a private message.
     */
    @POST("private_message")
    suspend fun createPrivateMessage(@Body form: CreatePrivateMessage): Response<PrivateMessageResponse>

    /**
     * Get your unread counts
     */
    @GET("user/unread_count")
    suspend fun getUnreadCount(@QueryMap form: Map<String, String>): Response<GetUnreadCountResponse>

    /**
     * Follow / subscribe to a community.
     */
    @POST("community/follow")
    suspend fun followCommunity(@Body form: FollowCommunity): Response<CommunityResponse>

    /**
     * Create a post.
     */
    @POST("post")
    suspend fun createPost(@Body form: CreatePost): Response<PostResponse>

    /**
     * Edit a post.
     */
    @PUT("post")
    suspend fun editPost(@Body form: EditPost): Response<PostResponse>

    /**
     * Delete a post.
     */
    @POST("post/delete")
    suspend fun deletePost(@Body form: DeletePost): Response<PostResponse>

    /**
     * Search lemmy.
     */
    @GET("search")
    suspend fun search(@QueryMap form: Map<String, String>): Response<SearchResponse>

    /**
     * Fetch metadata for any given site.
     */
    @GET("post/site_metadata")
    suspend fun getSiteMetadata(@QueryMap form: Map<String, String>): Response<GetSiteMetadataResponse>

    /**
     * Report a comment.
     */
    @POST("comment/report")
    suspend fun createCommentReport(@Body form: CreateCommentReport): Response<CommentReportResponse>

    /**
     * Report a post.
     */
    @POST("post/report")
    suspend fun createPostReport(@Body form: CreatePostReport): Response<PostReportResponse>

    /**
     * Block a person.
     */
    @POST("user/block")
    suspend fun blockPerson(@Body form: BlockPerson): Response<BlockPersonResponse>

    /**
     * Block a community.
     */
    @POST("community/block")
    suspend fun blockCommunity(@Body form: BlockCommunity): Response<BlockCommunityResponse>

    /**
     * Save your user settings.
     */
    @PUT("user/save_user_settings")
    suspend fun saveUserSettings(@Body form: SaveUserSettings): Response<LoginResponse>

    /**
     * Upload an image.
     */
    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Header("Cookie") token: String,
        @Part filePart: MultipartBody.Part,
    ): Response<PictrsImages>

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
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                        .header("User-Agent", "Jerboa")
                    val newRequest = requestBuilder.build()
                    chain.proceed(newRequest)
                }
                .addInterceptor(interceptor)
                .build()

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
    ctx: Context?,
): CommunityResponse? {
    var communityRes: CommunityResponse? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = FollowCommunity(
            community_id = communityView.community.id,
            follow = communityView.subscribed == SubscribedType.NotSubscribed,
            auth = auth,
        )
        communityRes = retrofitErrorHandler(api.followCommunity(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return communityRes
}

suspend fun getSiteWrapper(
    auth: String?,
    ctx: Context?,
): GetSiteResponse? {
    var siteRes: GetSiteResponse? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = GetSite(auth = auth)
        siteRes = retrofitErrorHandler(api.getSite(form = form.serializeToMap()))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return siteRes
}

suspend fun getSiteMetadataWrapper(
    url: String,
    ctx: Context?,
): SiteMetadata? {
    var res: SiteMetadata? = null
    val api = API.getInstance()

    try {
        // Fetch the site to get more info, such as your
        // name and avatar
        val form = GetSiteMetadata(url = url)
        res = retrofitErrorHandler(api.getSiteMetadata(form = form.serializeToMap())).metadata
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return res
}

suspend fun fetchPostsWrapper(
    account: Account?,
    ctx: Context?,
    communityIdOrName: Either<Int, String>? = null,
    sortType: SortType,
    listingType: ListingType,
    page: Int,

): List<PostView> {
    var posts = listOf<PostView>()
    val api = API.getInstance()

    val communityId = communityIdOrName?.fold({ it }, { null })
    val communityName = communityIdOrName?.fold({ null }, { it })

    try {
        val form = GetPosts(
            community_id = communityId,
            community_name = communityName,
            sort = sortType.toString(),
            type_ = listingType.toString(),
            page = page,
            auth = account?.jwt,
        )
        posts = retrofitErrorHandler(api.getPosts(form = form.serializeToMap())).posts
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
        res = retrofitErrorHandler(api.search(form = form.serializeToMap()))
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
            "Creating post: $form",
        )
        createdPostView = retrofitErrorHandler(api.createPost(form)).post_view
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
        editedPostView = retrofitErrorHandler(api.editPost(form)).post_view
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return editedPostView
}

suspend fun deletePostWrapper(
    form: DeletePost,
    ctx: Context,
): PostResponse? {
    var deletedPost: PostResponse? = null
    val api = API.getInstance()

    try {
        deletedPost = retrofitErrorHandler(api.deletePost(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return deletedPost
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
            post_id = pv.post.id,
            score = newVote,
            auth = account.jwt,
        )
        updatedPost = retrofitErrorHandler(api.likePost(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPost
}

suspend fun likeCommentWrapper(
    commentId: Int,
    myVote: Int?,
    voteType: VoteType,
    account: Account,
    ctx: Context,
): CommentResponse? {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val newVote = newVote(currentVote = myVote, voteType = voteType)
        val form = CreateCommentLike(
            comment_id = commentId,
            score = newVote,
            auth = account.jwt,
        )
        updatedComment = retrofitErrorHandler(api.likeComment(form))
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
            post_id = pv.post.id,
            save = !pv.saved,
            auth = account.jwt,
        )
        updatedPost = retrofitErrorHandler(api.savePost(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedPost
}

suspend fun saveCommentWrapper(
    commentId: Int,
    saved: Boolean,
    account: Account,
    ctx: Context,
): CommentResponse? {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val form = SaveComment(
            comment_id = commentId,
            save = !saved,
            auth = account.jwt,
        )
        updatedComment = retrofitErrorHandler(api.saveComment(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return updatedComment
}

suspend fun markCommentReplyAsReadWrapper(
    crv: CommentReplyView,
    account: Account,
    ctx: Context,
): CommentResponse? {
    var updatedComment: CommentResponse? = null
    val api = API.getInstance()
    try {
        val form = MarkCommentReplyAsRead(
            comment_reply_id = crv.comment_reply.id,
            read = !crv.comment_reply.read,
            auth = account.jwt,
        )
        updatedComment = retrofitErrorHandler(api.markCommentReplyAsRead(form))
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
                .jwt,
        )
        updatedPm = retrofitErrorHandler(api.markPersonMentionAsRead(form))
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
            private_message_id = pm.private_message.id,
            read = !pm.private_message.read,
            auth = account
                .jwt,
        )
        updatedPm = retrofitErrorHandler(api.markPrivateMessageAsRead(form))
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
        createdComment = retrofitErrorHandler(api.createComment(form))
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
        editedComment = retrofitErrorHandler(api.editComment(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return editedComment
}

suspend fun deleteCommentWrapper(
    form: DeleteComment,
    ctx: Context,
): CommentResponse? {
    var deletedComment: CommentResponse? = null
    val api = API.getInstance()

    try {
        deletedComment = retrofitErrorHandler(api.deleteComment(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return deletedComment
}

suspend fun createPrivateMessageWrapper(
    form: CreatePrivateMessage,
    ctx: Context,
): PrivateMessageResponse? {
    var createdPrivateMessage: PrivateMessageResponse? = null
    val api = API.getInstance()

    try {
        createdPrivateMessage = retrofitErrorHandler(api.createPrivateMessage(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdPrivateMessage
}

suspend fun createCommentReportWrapper(
    form: CreateCommentReport,
    ctx: Context,
): CommentReportResponse? {
    var createdReport: CommentReportResponse? = null
    val api = API.getInstance()

    try {
        createdReport = retrofitErrorHandler(api.createCommentReport(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdReport
}

suspend fun createPostReportWrapper(
    form: CreatePostReport,
    ctx: Context,
): PostReportResponse? {
    var createdReport: PostReportResponse? = null
    val api = API.getInstance()

    try {
        createdReport = retrofitErrorHandler(api.createPostReport(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return createdReport
}

suspend fun blockPersonWrapper(
    form: BlockPerson,
    ctx: Context,
): BlockPersonResponse? {
    var blockPersonRes: BlockPersonResponse? = null
    val api = API.getInstance()

    try {
        blockPersonRes = retrofitErrorHandler(api.blockPerson(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return blockPersonRes
}

suspend fun blockCommunityWrapper(
    form: BlockCommunity,
    ctx: Context,
): BlockCommunityResponse? {
    var blockCommunityRes: BlockCommunityResponse? = null
    val api = API.getInstance()

    try {
        blockCommunityRes = retrofitErrorHandler(api.blockCommunity(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return blockCommunityRes
}

suspend fun saveUserSettingsWrapper(
    form: SaveUserSettings,
    ctx: Context,
): LoginResponse? {
    var saveUserSettingsResponse: LoginResponse? = null
    val api = API.getInstance()

    try {
        saveUserSettingsResponse = retrofitErrorHandler(api.saveUserSettings(form))
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return saveUserSettingsResponse
}

suspend fun uploadPictrsImage(account: Account, imageIs: InputStream, ctx: Context): String? {
    var imageUrl: String? = null
    val api = API.getInstance()
    try {
        Log.d("jerboa", "Uploading image....")
        val part = MultipartBody.Part.createFormData(
            "images[]",
            "myPic",
            imageIs.readBytes().toRequestBody(),
        )
        val url = "https://${API.currentInstance}/pictrs/image"
        val cookie = "jwt=${account.jwt}"
        val images = retrofitErrorHandler(api.uploadImage(url, cookie, part))
        Log.d("jerboa", "Uploading done.")
        imageUrl = "$url/${images.files?.get(0)?.file}"
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return imageUrl
}

fun <T> retrofitErrorHandler(res: Response<T>): T {
    if (res.isSuccessful) {
        return res.body()!!
    } else {
        val errMsg = res.errorBody()?.string()?.let {
            JSONObject(it).getString("error")
        } ?: run {
            res.code().toString()
        }

        throw Exception(errMsg)
    }
}
