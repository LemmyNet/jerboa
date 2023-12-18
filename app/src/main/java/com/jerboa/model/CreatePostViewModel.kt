package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePost
import it.vercruysse.lemmyapi.v0x19.datatypes.GetSiteMetadata
import it.vercruysse.lemmyapi.v0x19.datatypes.GetSiteMetadataResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PostResponse
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    var createPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
        private set
    var siteMetadataRes: ApiState<GetSiteMetadataResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createPost(
        form: CreatePost,
        onSuccess: (postId: Int) -> Unit,
    ) {
        viewModelScope.launch {
            createPostRes = ApiState.Loading
            createPostRes = API.getInstance().createPost(form).toApiState()

            when (val postRes = createPostRes) {
                is ApiState.Success -> {
                    onSuccess(postRes.data.post_view.post.id)
                }
                else -> {}
            }
        }
    }

    fun getSiteMetadata(form: GetSiteMetadata) {
        viewModelScope.launch {
            siteMetadataRes = ApiState.Loading
            siteMetadataRes = API.getInstance().getSiteMetadata(form).toApiState()
        }
    }
}
