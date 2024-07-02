package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostResponse
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.RemovePost
import kotlinx.coroutines.launch

class PostRemoveViewModel : ViewModel() {
    var postRemoveRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun removeOrRestorePost(
        postId: PostId,
        removed: Boolean,
        reason: String,
        ctx: Context,
        focusManager: FocusManager,
        onSuccess: (PostView) -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                RemovePost(
                    post_id = postId,
                    removed = removed,
                    reason = reason,
                )

            postRemoveRes = ApiState.Loading
            postRemoveRes = API.getInstance().removePost(form).toApiState()

            when (val res = postRemoveRes) {
                is ApiState.Failure -> {
                    Log.d("removePost", "failed", res.msg)
                    apiErrorToast(msg = res.msg, ctx = ctx)
                }

                is ApiState.Success -> {
                    val message =
                        if (removed) {
                            ctx.getString(R.string.post_removed)
                        } else {
                            ctx.getString(R.string.post_restored)
                        }
                    val postView = res.data.post_view
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()

                    focusManager.clearFocus()
                    onSuccess(postView)
                }
                else -> {}
            }
        }
    }
}
