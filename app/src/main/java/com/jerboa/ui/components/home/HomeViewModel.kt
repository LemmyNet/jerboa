package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.datatypes.CommentReplyView
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetUnreadCount
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.community.blockCommunityRoutine
import com.jerboa.ui.components.person.blockPersonRoutine
import com.jerboa.ui.components.post.deletePostRoutine
import com.jerboa.ui.components.post.fetchPostsRoutine
import com.jerboa.ui.components.post.likePostRoutine
import com.jerboa.ui.components.post.savePostRoutine
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var posts = mutableStateListOf<PostView>()
    var loading = mutableStateOf(false)
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(SortType.Active)
        private set
    var listingType = mutableStateOf(ListingType.Local)
        private set
    var unreadCountResponse by mutableStateOf<GetUnreadCountResponse?>(null)
        private set

    fun fetchPosts(
        account: Account?,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeListingType: ListingType? = null,
        changeSortType: SortType? = null,
        ctx: Context? = null,
    ) {
        fetchPostsRoutine(
            posts = posts,
            loading = loading,
            page = page,
            listingType = listingType,
            sortType = sortType,
            nextPage = nextPage,
            clear = clear,
            changeListingType = changeListingType,
            changeSortType = changeSortType,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun likePost(voteType: VoteType, postView: PostView, account: Account?, ctx: Context) {
        likePostRoutine(mutableStateOf(postView), posts, voteType, account, ctx, viewModelScope)
    }

    fun savePost(postView: PostView, account: Account, ctx: Context) {
        savePostRoutine(mutableStateOf(postView), posts, account, ctx, viewModelScope)
    }

    fun deletePost(postView: PostView, account: Account, ctx: Context) {
        deletePostRoutine(mutableStateOf(postView), posts, account, ctx, viewModelScope)
    }

    fun fetchUnreadCounts(
        account: Account,
        ctx: Context? = null,
    ) {
        viewModelScope.launch {
            try {
                val api = API.getInstance()
                val form = GetUnreadCount(
                    auth = account.jwt,
                )
                Log.d(
                    "jerboa",
                    "Fetching unread counts: $form",
                )
                unreadCountResponse = retrofitErrorHandler(
                    api.getUnreadCount(
                        form = form
                            .serializeToMap(),
                    ),
                )
            } catch (e: Exception) {
                toastException(ctx = ctx, error = e)
            }
        }
    }

    fun updateUnreads(commentReplyView: CommentReplyView) {
        val inc = incrementFromRead(commentReplyView.comment_reply.read)
        val newReplyCount = unreadCountResponse!!.replies + inc
        unreadCountResponse = unreadCountResponse?.copy(replies = newReplyCount)
    }

    fun updateUnreads(privateMessageView: PrivateMessageView) {
        val inc = incrementFromRead(privateMessageView.private_message.read)
        val newPmCount = unreadCountResponse!!.private_messages + inc
        unreadCountResponse = unreadCountResponse?.copy(private_messages = newPmCount)
    }

    fun updateUnreads(personMentionView: PersonMentionView) {
        val inc = incrementFromRead(personMentionView.person_mention.read)
        val newMentionCount = unreadCountResponse!!.mentions + inc
        unreadCountResponse = unreadCountResponse?.copy(mentions = newMentionCount)
    }

    fun markAllAsRead() {
        unreadCountResponse = unreadCountResponse?.copy(
            replies = 0,
            private_messages = 0,
            mentions = 0,
        )
    }

    fun blockCommunity(
        community: CommunitySafe,
        account: Account,
        ctx: Context,
    ) {
        blockCommunityRoutine(
            community = community,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun blockCreator(
        creator: PersonSafe,
        account: Account,
        ctx: Context,
    ) {
        blockPersonRoutine(
            person = creator,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    //update the listing type when a user selects all/local/subscribed from a menu source
    //so we can default to that when the app reopens
    fun updateListingType(listingType: String, ctx: Context) {
        this.listingType.value = ListingType.valueOf(listingType)
        val prefs = ctx.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        prefs.edit {
            putString("DEFAULT_LISTING_TYPE", listingType)
        }
    }
}

fun incrementFromRead(read: Boolean): Int {
    return if (read) {
        1
    } else {
        -1
    }
}
