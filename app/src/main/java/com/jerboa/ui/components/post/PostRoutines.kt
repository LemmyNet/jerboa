package com.jerboa.ui.components.post

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.jerboa.VoteType
import com.jerboa.api.fetchPostsWrapper
import com.jerboa.api.likePostWrapper
import com.jerboa.api.savePostWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.SortType
import com.jerboa.db.Account
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
    ctx: Context? = null,
    scope: CoroutineScope,
) {
    scope.launch {
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

        val newPosts = fetchPostsWrapper(
            account = account,
            ctx = ctx,
            communityId = communityId.value,
            sortType = sortType.value,
            listingType = listingType.value,
            page = page.value
        )

        if (clear) {
            posts.clear()
        }
        posts.addAll(newPosts)
        loading.value = false
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
