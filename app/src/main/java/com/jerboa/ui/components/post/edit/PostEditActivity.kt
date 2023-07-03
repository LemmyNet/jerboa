
package com.jerboa.ui.components.post.edit

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.EditPost
import com.jerboa.datatypes.types.PostView
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.addReturn
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.ui.components.post.composables.CreateEditPostHeader
import com.jerboa.ui.components.post.composables.EditPostSubmitIcon
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import kotlinx.coroutines.launch

object PostEditReturn {
    const val POST_VIEW = "post-edit::return(post-view)"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEditActivity(
    postView: PostView,
    accountViewModel: AccountViewModel,
    navController: NavController,
) {
    Log.d("jerboa", "got to post edit activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    val postEditViewModel: PostEditViewModel = viewModel()
    InitializeRoute(postEditViewModel) {
        postEditViewModel.initialize(postView)
    }

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

    val nameField = validatePostName(name)
    val urlField = validateUrl(url)
    val formValid = !nameField.hasError && !urlField.hasError

    Scaffold(
        topBar = {
            Column {
                val loading = when (postEditViewModel.editPostRes) {
                    ApiState.Loading -> true
                    else -> false
                }
                CreateEditPostHeader(
                    navController = navController,
                    formValid = formValid,
                    loading = loading,
                    submitIcon = {
                        EditPostSubmitIcon()
                    },
                    title = stringResource(R.string.post_edit_edit_post),
                    onSubmitClick = {
                        onSubmitClick(
                            account = account,
                            name = name,
                            body = body,
                            url = url,
                            postEditViewModel = postEditViewModel,
                            isNsfw = isNsfw,
                            navController = navController,
                        )
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
                    val imageIs = imageInputStreamFromUri(ctx, uri)
                    scope.launch {
                        isUploadingImage = true
                        account?.also { acct ->
                            url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                        }
                        isUploadingImage = false
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
    account: Account?,
    name: String,
    body: TextFieldValue,
    url: String,
    postEditViewModel: PostEditViewModel,
    isNsfw: Boolean,
    navController: NavController,
) {
    account?.also { acct ->
        // Clean up that data
        val nameOut = name.trim()
        val bodyOut = body.text.trim().ifEmpty { null }
        val urlOut = url.trim().ifEmpty { null }
        val pv = postEditViewModel.postView

        postEditViewModel.editPost(
            form = EditPost(
                post_id = pv!!.post.id,
                name = nameOut,
                url = urlOut,
                body = bodyOut,
                auth = acct.jwt,
                nsfw = isNsfw,
            ),
        ) { postView ->
            navController.apply {
                addReturn(PostEditReturn.POST_VIEW, postView)
                navigateUp()
            }
        }
    }
}
