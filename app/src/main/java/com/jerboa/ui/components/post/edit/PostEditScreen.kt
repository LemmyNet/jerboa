package com.jerboa.ui.components.post.edit

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.AccountViewModel
import com.jerboa.model.PostEditViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import it.vercruysse.lemmyapi.datatypes.EditPost
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView
import kotlinx.coroutines.launch

object PostEditReturn {
    const val POST_VIEW = "post-edit::return(post-view)"
    const val POST_SEND = "post-edit::send(post-view)"
}

@Composable
fun PostEditScreen(
    accountViewModel: AccountViewModel,
    appState: JerboaAppState,
) {
    Log.d("jerboa", "got to post edit screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    val postEditViewModel: PostEditViewModel = viewModel()

    val postView = appState.getPrevReturn<PostView>(PostEditReturn.POST_SEND)

    var name by rememberSaveable { mutableStateOf(postView.post.name) }
    var url by rememberSaveable { mutableStateOf(postView.post.url.orEmpty()) }
    var isNsfw by rememberSaveable { mutableStateOf(postView.post.nsfw) }
    var body by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                postView.post.body.orEmpty(),
            ),
        )
    }
    var isUploadingImage by rememberSaveable { mutableStateOf(false) }
    var isUploadingCustomThumbnailImage by rememberSaveable { mutableStateOf(false) }
    var altText by rememberSaveable { mutableStateOf(postView.post.alt_text.orEmpty()) }
    var customThumbnail by rememberSaveable { mutableStateOf(postView.post.thumbnail_url.orEmpty()) }

    val nameField = validatePostName(ctx, name)
    val urlField = validateUrl(ctx, url)
    val customThumbnailField = validateUrl(ctx, customThumbnail, ctx.getString(R.string.custom_thumbnail))
    val formValid = !nameField.hasError && !urlField.hasError && !customThumbnailField.hasError

    Scaffold(
        topBar = {
            Column {
                val loading =
                    when (postEditViewModel.editPostRes) {
                        ApiState.Loading -> true
                        else -> false
                    }
                ActionTopBar(
                    onBackClick = appState::popBackStack,
                    formValid = formValid,
                    loading = loading,
                    title = stringResource(R.string.post_edit_edit_post),
                    onActionClick = {
                        if (!account.isAnon()) {
                            onSubmitClick(
                                postId = postView.post.id,
                                name = name,
                                body = body,
                                url = url,
                                altText = altText,
                                customThumbnail = customThumbnail,
                                postEditViewModel = postEditViewModel,
                                isNsfw = isNsfw,
                                appState = appState,
                            )
                        }
                    },
                )
                if (loading) {
                    LoadingBar()
                }
            }
        },
        content = { padding ->
            CreateEditPostBody(
                name = name,
                nameField = nameField,
                onNameChange = { name = it },
                body = body,
                onBodyChange = { body = it },
                url = url,
                urlField = urlField,
                onUrlChange = { url = it },
                onImagePicked = { uri ->
                    if (!account.isAnon()) {
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            isUploadingImage = true
                            url = API.uploadPictrsImage(imageIs, ctx)
                            isUploadingImage = false
                        }
                    }
                },
                isUploadingImage = isUploadingImage,
                altText = altText,
                onAltTextChange = { altText = it },
                customThumbnailField = customThumbnailField,
                customThumbnail = customThumbnail,
                onCustomThumbnailChange = { customThumbnail = it },
                isUploadingCustomThumbnailImage = isUploadingCustomThumbnailImage,
                onCustomThumbnailImagePicked = { uri ->
                    if (!account.isAnon() && uri != Uri.EMPTY) {
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            isUploadingCustomThumbnailImage = true
                            customThumbnail = API.uploadPictrsImage(imageIs, ctx)
                            isUploadingCustomThumbnailImage = false
                        }
                    }
                },
                account = account,
                padding = padding,
                isNsfw = isNsfw,
                onIsNsfwChange = { isNsfw = it },
                communitySelector = {},
                error = when (val res = postEditViewModel.editPostRes) {
                    is ApiState.Failure -> res.msg
                    else -> null
                },
            )
        },
    )
}

fun onSubmitClick(
    postId: PostId,
    name: String,
    body: TextFieldValue,
    url: String,
    altText: String,
    customThumbnail: String,
    postEditViewModel: PostEditViewModel,
    isNsfw: Boolean,
    appState: JerboaAppState,
) {
    // Clean up that data
    val nameOut = name.trim()
    val bodyOut = body.text.trim().ifEmpty { null }
    val urlOut = url.trim().ifEmpty { null }
    val altTextOut = altText.trim().ifEmpty { null }
    val customThumbnailOut = customThumbnail.trim().ifEmpty { null }

    postEditViewModel.editPost(
        form =
            EditPost(
                post_id = postId,
                name = nameOut,
                url = urlOut,
                alt_text = altTextOut,
                custom_thumbnail = customThumbnailOut,
                body = bodyOut,
                nsfw = isNsfw,
            ),
    ) { postView ->
        appState.apply {
            addReturn(PostEditReturn.POST_VIEW, postView)
            navigateUp()
        }
    }
}
