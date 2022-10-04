package com.jerboa.ui.components.post.edit

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.api.editPostWrapper
import com.jerboa.datatypes.PostView
import com.jerboa.db.Account
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.findAndUpdatePost
import kotlinx.coroutines.launch

class PostEditViewModel : ViewModel() {

    var postView by mutableStateOf<PostView?>(null)
        private set
    var loading by mutableStateOf(false)
        private set

    fun initialize(
        newPostView: PostView
    ) {
        postView = newPostView
    }

    fun editPost(
        name: String,
        body: String?,
        url: String?,
        ctx: Context,
        navController: NavController,
        account: Account,
        personProfileViewModel: PersonProfileViewModel,
        postViewModel: PostViewModel,
        communityViewModel: CommunityViewModel,
        homeViewModel: HomeViewModel
    ) {
        viewModelScope.launch {
            postView?.also { pv ->
                loading = true
                postView = editPostWrapper(
                    postView = pv,
                    account = account,
                    body = body,
                    url = url,
                    name = name,
                    ctx = ctx
                )
                postViewModel.postView.value = postView
                findAndUpdatePost(personProfileViewModel.posts, postView)
                findAndUpdatePost(communityViewModel.posts, postView)
                findAndUpdatePost(homeViewModel.posts, postView)

                loading = false
                navController.popBackStack()
            }
        }
    }
}
