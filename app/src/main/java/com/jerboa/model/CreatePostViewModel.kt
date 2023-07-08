package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CreatePost
import com.jerboa.datatypes.types.GetSiteMetadata
import com.jerboa.datatypes.types.GetSiteMetadataResponse
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.serializeToMap
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
            createPostRes =
                apiWrapper(
                    API.getInstance().createPost(form),
                )

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
            siteMetadataRes =
                apiWrapper(
                    API.getInstance().getSiteMetadata(form.serializeToMap()),
                )
        }
    }
}
