
package com.jerboa.ui.components.post.edit

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
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.EditPost
import com.jerboa.datatypes.types.PostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.AccountViewModel
import com.jerboa.model.PostEditViewModel
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SaveTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import kotlinx.coroutines.launch

object PostEditReturn {
    const val POST_VIEW = "post-edit::return(post-view)"
    const val POST_SEND = "post-edit::send(post-view)"
}

@Composable
fun PostEditActivity(
    accountViewModel: AccountViewModel,
    appState: JerboaAppState,
) {
    Log.d("jerboa", "got to post edit activity")

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

    val nameField = validatePostName(ctx, name)
    val urlField = validateUrl(ctx, url)
    val formValid = !nameField.hasError && !urlField.hasError

    Scaffold(
        topBar = {
            Column {
                val loading = when (postEditViewModel.editPostRes) {
                    ApiState.Loading -> true
                    else -> false
                }
                SaveTopBar(
                    onBackClick = appState::popBackStack,
                    formValid = formValid,
                    loading = loading,
                    title = stringResource(R.string.post_edit_edit_post),
                    onSaveClick = {
                        if (!account.isAnon()) {
                            onSubmitClick(
                                postId = postView.post.id,
                                account = account,
                                name = name,
                                body = body,
                                url = url,
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
                            url = uploadPictrsImage(account, imageIs, ctx).orEmpty()
                            isUploadingImage = false
                        }
                    }
                },
                isUploadingImage = isUploadingImage,
                account = account,
                padding = padding,
                isNsfw = isNsfw,
                onIsNsfwChange = { isNsfw = it },
                communitySelector = {},
            )
        },
    )
}

fun onSubmitClick(
    postId: Int,
    account: Account,
    name: String,
    body: TextFieldValue,
    url: String,
    postEditViewModel: PostEditViewModel,
    isNsfw: Boolean,
    appState: JerboaAppState,
) {
    // Clean up that data
    val nameOut = name.trim()
    val bodyOut = body.text.trim().ifEmpty { null }
    val urlOut = url.trim().ifEmpty { null }

    postEditViewModel.editPost(
        form = EditPost(
            post_id = postId,
            name = nameOut,
            url = urlOut,
            body = bodyOut,
            auth = account.jwt,
            nsfw = isNsfw,
        ),
    ) { postView ->
        appState.apply {
            addReturn(PostEditReturn.POST_VIEW, postView)
            navigateUp()
        }
    }
}
