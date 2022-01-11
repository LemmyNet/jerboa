package com.jerboa.ui.components.post

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.likePostWrapper
import com.jerboa.api.savePostWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun fetchPostsRoutine(
    posts: MutableList<PostView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    communityId: MutableState<Int?> = mutableStateOf(null),
    listingType: MutableState<ListingType>,
    sortType: MutableState<SortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeCommunityId: Int? = null,
    changeListingType: ListingType? = null,
    changeSortType: SortType? = null,
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
            val api = API.getInstance()
            try {
                loading.value = true

                if (nextPage) {
                    page.value++
                }

                if (clear) {
                    page.value = 1
                }

                changeListingType?.also {
                    listingType.value = it
                }

                changeSortType?.also {
                    sortType.value = it
                }

                changeCommunityId?.also {
                    communityId.value = it
                }

                val form = GetPosts(
                    community_id = communityId.value,
                    sort = sortType.value.toString(),
                    type_ = listingType.value.toString(),
                    page = page.value,
                    auth = account.jwt,
                )
                Log.d(
                    "jerboa",
                    "Fetching posts: $form"
                )
                val newPosts = api.getPosts(form = form.serializeToMap()).posts

                if (clear) {
                    posts.clear()
                }
                posts.addAll(newPosts)
            } catch (e: Exception) {
                toastException(ctx = ctx, error = e)
            } finally {
                loading.value = false
            }
        }
    }
}

fun likePostRoutine(
    postView: MutableState<PostView?>,
    posts: MutableList<PostView>? = null,
    voteType: VoteType,
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
            postView.value?.also { pv ->
                val updatedPostView = likePostWrapper(
                    pv, voteType, account,
                    ctx
                ).post_view
                postView.value = updatedPostView
                posts?.also {
                    findAndUpdatePost(posts, updatedPostView)
                }
            }
        }
    }
}

fun savePostRoutine(
    postView: MutableState<PostView?>,
    posts: MutableList<PostView>? = null,
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
            postView.value?.also { pv ->
                val updatedPostView = savePostWrapper(
                    pv,
                    account,
                    ctx,
                ).post_view
                postView.value = updatedPostView
                posts?.also {
                    findAndUpdatePost(posts, updatedPostView)
                }
            }
        }
    }
}

fun findAndUpdatePost(posts: MutableList<PostView>, updatedPostView: PostView?) {
    updatedPostView?.also { upv ->
        val foundIndex = posts.indexOfFirst {
            it.post.id == upv.post.id
        }
        if (foundIndex != -1) {
            posts[foundIndex] = upv
        }
    }
}
