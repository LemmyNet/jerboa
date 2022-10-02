package com.jerboa.ui.components.community

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.followCommunityWrapper
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.GetCommunity
import com.jerboa.db.Account
import com.jerboa.loginFirstToast
import com.jerboa.serializeToMap
import com.jerboa.ui.components.person.blockPersonRoutine
import com.jerboa.ui.components.post.deletePostRoutine
import com.jerboa.ui.components.post.fetchPostsRoutine
import com.jerboa.ui.components.post.likePostRoutine
import com.jerboa.ui.components.post.savePostRoutine
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {

    var communityView by mutableStateOf<CommunityView?>(null)
        private set
    var moderators = mutableStateListOf<CommunityModeratorView>()
        private set

    var communityId = mutableStateOf<Int?>(null)
    var loading = mutableStateOf(false)
        private set
    var posts = mutableStateListOf<PostView>()
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(SortType.Active)
        private set

    fun fetchPosts(
        account: Account?,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeSortType: SortType? = null,
        changeCommunityId: Int? = null,
        ctx: Context
    ) {
        fetchPostsRoutine(
            posts = posts,
            loading = loading,
            page = page,
            communityId = communityId,
            listingType = mutableStateOf(ListingType.Community),
            sortType = sortType,
            nextPage = nextPage,
            clear = clear,
            changeSortType = changeSortType,
            changeCommunityId = changeCommunityId,
            account = account,
            ctx = ctx,
            scope = viewModelScope
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

    fun followCommunity(
        cv: CommunityView,
        account: Account?,
        ctx: Context
    ) {
        viewModelScope.launch {
            account?.also { acct ->
                communityView =
                    followCommunityWrapper(communityView = cv, auth = acct.jwt, ctx = ctx)?.community_view
            } ?: run {
                loginFirstToast(ctx)
            }
        }
    }

    fun fetchCommunity(id: Int, auth: String?) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching community id: $id"
                )
                loading.value = true

                val form = GetCommunity(id = id, auth = auth)
                val out = retrofitErrorHandler(api.getCommunity(form = form.serializeToMap()))
                communityView = out.community_view
                communityId.value = id
                moderators.clear()
                moderators.addAll(out.moderators)
            } catch (e: Exception) {
                Log.e(
                    "jerboa",
                    e.toString()
                )
            } finally {
                loading.value = false
            }
        }
    }

    fun blockCommunity(
        account: Account,
        ctx: Context
    ) {
        communityView?.community?.also {
            blockCommunityRoutine(
                community = it,
                block = true,
                account = account,
                ctx = ctx,
                scope = viewModelScope
            )
        }
    }

    fun blockCreator(
        creator: PersonSafe,
        account: Account,
        ctx: Context
    ) {
        blockPersonRoutine(
            person = creator,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }
}
