package com.jerboa.model

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
            editPostRes =
                apiWrapper(
                    API.getInstance().editPost(form),
                )

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
