package com.jerboa.ui.components.post.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.EditPost
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class PostEditViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

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
        onSuccess: (PostView) -> Unit,
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
                    onSuccess(post)
                }
                else -> {}
            }
        }
    }
}
