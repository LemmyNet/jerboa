package com.jerboa.ui.components.post

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.likePostWrapper
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.GetPost
import com.jerboa.datatypes.api.GetPostResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    var res by mutableStateOf<GetPostResponse?>(null)
        private set
    var postView by mutableStateOf<PostView?>(null)
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun fetchPost(form: GetPost) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching post: $form"
                )
                loading = true
                res = api.getPost(form = form.serializeToMap())
                postView = res?.post_view
            } catch (e: Exception) {
                Log.e(
                    "jerboa",
                    e.toString(),
                )
            } finally {
                loading = false
            }
        }
    }

    fun likePost(
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        account?.let { acct ->
            postView?.let { pv ->
                viewModelScope.launch {
                    postView = likePostWrapper(pv, voteType, acct, ctx).post_view
                }
            }
        }
    }
}
