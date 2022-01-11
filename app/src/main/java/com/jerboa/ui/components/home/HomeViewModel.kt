package com.jerboa.ui.components.home

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.VoteType
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.SortType
import com.jerboa.db.Account
import com.jerboa.ui.components.post.fetchPostsRoutine
import com.jerboa.ui.components.post.likePostRoutine
import com.jerboa.ui.components.post.savePostRoutine

class HomeViewModel : ViewModel() {

    var posts = mutableStateListOf<PostView>()
        private set
    var loading = mutableStateOf(false)
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(SortType.Active)
        private set
    var listingType = mutableStateOf(ListingType.All)
        private set

    fun fetchPosts(
        account: Account?,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeListingType: ListingType? = null,
        changeSortType: SortType? = null,
        ctx: Context,

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

    fun savePost(postView: PostView, account: Account?, ctx: Context) {
        savePostRoutine(mutableStateOf(postView), posts, account, ctx, viewModelScope)
    }
}
