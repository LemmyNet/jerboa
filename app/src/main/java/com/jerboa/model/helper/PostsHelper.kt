package com.jerboa.model.helper

import com.jerboa.api.API
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface PostsHelper {
    val scope: CoroutineScope

    fun updatePost(postView: PostView)

    fun likePost(form: CreatePostLike) {
        scope.launch {
            API.getInstance().createPostLike(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun savePost(form: SavePost) {
        scope.launch {
            API.getInstance().savePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun deletePost(form: DeletePost) {
        scope.launch {
            API.getInstance().deletePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }
}
