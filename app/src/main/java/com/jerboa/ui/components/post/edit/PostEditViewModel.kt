package com.jerboa.ui.components.post.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.EditPost
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

class PostEditViewModel : ViewModel() {
    var postView by mutableStateOf<PostView?>(null)
        private set
    var editPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun initialize(
        newPostView: PostView,
    ) {
        postView = newPostView
    }

    fun editPost(
        form: EditPost,
        navController: NavController,
        personProfileViewModel: PersonProfileViewModel,
        postViewModel: PostViewModel,
        communityViewModel: CommunityViewModel,
        homeViewModel: HomeViewModel,
    ) {
        viewModelScope.launch {
            editPostRes = ApiState.Loading
            editPostRes =
                apiWrapper(
                    API.getInstance().editPost(form),
                )

            when (val res = editPostRes) {
                is ApiState.Success -> {
                    val post = res.data.post_view
                    postView = post

                    // Update the other view models
                    postViewModel.updatePost(post)
                    personProfileViewModel.updatePost(post)
                    communityViewModel.updatePost(post)
                    homeViewModel.updatePost(post)

                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }
}
