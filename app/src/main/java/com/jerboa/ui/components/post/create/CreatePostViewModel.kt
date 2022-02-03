package com.jerboa.ui.components.post.create

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.api.createPostWrapper
import com.jerboa.api.getSiteMetadataWrapper
import com.jerboa.db.Account
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    var suggestedTitle by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
        private set

    fun createPost(
        account: Account,
        ctx: Context,
        body: String?,
        url: String?,
        name: String,
        communityId: Int,
        navController: NavController,
        postViewModel: PostViewModel,
    ) {
        viewModelScope.launch {
            loading = true
            val postOut = createPostWrapper(
                account = account,
                communityId = communityId,
                body = body,
                url = url,
                name = name,
                ctx = ctx,
            )
            // TODO not sure here
            postViewModel.fetchPost(
                id = postOut!!.post.id, clear = true, account = account,
                ctx = ctx
            )
            loading = false
            navController.popBackStack()
            navController.navigate("post")
        }
    }

    fun fetchSuggestedTitle(url: String, ctx: Context) {
        viewModelScope.launch {
            suggestedTitle = getSiteMetadataWrapper(url, ctx)?.title
        }
    }
}
