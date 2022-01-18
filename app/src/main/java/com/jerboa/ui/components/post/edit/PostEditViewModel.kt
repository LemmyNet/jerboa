package com.jerboa.ui.components.post.edit

import android.content.Context
import androidx.compose.runtime.mutableStateOf
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

    var postView = mutableStateOf<PostView?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun setPostView(
        newPostView: PostView
    ) {
        postView.value = newPostView
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
        homeViewModel: HomeViewModel,
    ) {
        viewModelScope.launch {
            postView.value?.also { pv ->
                postView.value = editPostWrapper(
                    postView = pv,
                    account = account,
                    body = body,
                    url = url,
                    name = name,
                    ctx = ctx,
                )
                postViewModel.postView.value = postView.value
                findAndUpdatePost(personProfileViewModel.posts, postView.value)
                findAndUpdatePost(communityViewModel.posts, postView.value)
                findAndUpdatePost(homeViewModel.posts, postView.value)

                navController.popBackStack()
            }
        }
    }
}
