package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import it.vercruysse.lemmyapi.datatypes.EditPost
import it.vercruysse.lemmyapi.datatypes.PostResponse
import it.vercruysse.lemmyapi.datatypes.PostView
import kotlinx.coroutines.launch

class PostEditViewModel : ViewModel() {
    var editPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun editPost(
        form: EditPost,
        onSuccess: (PostView) -> Unit,
    ) {
        viewModelScope.launch {
            editPostRes = ApiState.Loading
            editPostRes = API.getInstance().editPost(form).toApiState()

            when (val res = editPostRes) {
                is ApiState.Success -> {
                    val post = res.data.post_view
                    onSuccess(post)
                }

                else -> {}
            }
        }
    }
}
