package com.jerboa.ui.components.post

import android.content.Context
import androidx.compose.runtime.MutableState
import arrow.core.Either
import com.jerboa.api.deletePostWrapper
import com.jerboa.api.fetchPostsWrapper
import com.jerboa.api.likePostWrapper
import com.jerboa.api.savePostWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.DeletePost
import com.jerboa.db.Account
import com.jerboa.util.VoteType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun fetchPostsRoutine(
    posts: MutableList<PostView>,
    communityIdOrName: Either<Int, String>? = null,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    listingType: MutableState<ListingType>,
    sortType: MutableState<SortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
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

        val newPosts = fetchPostsWrapper(
            account = account,
            ctx = ctx,
            communityIdOrName = communityIdOrName,
            sortType = sortType.value,
            listingType = listingType.value,
            page = page.value,
        )

        if (clear) {
            posts.clear()
        }

        val newPostsDeduped = newPosts.filterNot { pv ->
            posts.map { op -> op.post.id }.contains(
                pv
                    .post.id,
            )
        }

        posts.addAll(newPostsDeduped)

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
                    pv,
                    voteType,
                    account,
                    ctx,
                )?.post_view
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
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        postView.value?.also { pv ->
            val updatedPostView = savePostWrapper(
                pv,
                account,
                ctx,
            )?.post_view
            postView.value = updatedPostView
            posts?.also {
                findAndUpdatePost(posts, updatedPostView)
            }
        }
    }
}

fun deletePostRoutine(
    postView: MutableState<PostView?>,
    posts: MutableList<PostView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        postView.value?.also { pv ->
            val form = DeletePost(
                post_id = pv.post.id,
                deleted = !pv.post.deleted,
                auth = account.jwt,
            )
            val deletedPostView = deletePostWrapper(form, ctx)?.post_view
            postView.value = deletedPostView
            posts?.also {
                findAndUpdatePost(posts, deletedPostView)
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
