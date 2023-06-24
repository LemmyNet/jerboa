
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
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.EditPost
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.isImage
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.ui.components.post.composables.CreateEditPostHeader
import com.jerboa.ui.components.post.composables.EditPostSubmitIcon
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import kotlinx.coroutines.launch
import java.net.URI

@Composable
fun PostEditActivity(
    accountViewModel: AccountViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    postViewModel: PostViewModel,
    personProfileViewModel: PersonProfileViewModel,
    communityViewModel: CommunityViewModel,
    homeViewModel: HomeViewModel,
) {
    Log.d("jerboa", "got to post edit activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()
    val pv = postEditViewModel.postView
    val postIsImage = pv?.post?.url?.let { isImage(it) } ?: run { false }

    var name by rememberSaveable { mutableStateOf(pv?.post?.name.orEmpty()) }
    var url by rememberSaveable { mutableStateOf(pv?.post?.url.orEmpty()) }
    var isNsfw by rememberSaveable { mutableStateOf(pv?.post?.nsfw ?: false) }
    var body by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                pv?.post?.body.orEmpty(),
            ),
        )
    }

    val nameField = validatePostName(name)
    val urlField = validateUrl(url)
    val formValid = !nameField.hasError && !urlField.hasError

    val image: Uri? = if(postIsImage){
        Uri.parse(pv!!.post.url)
    } else {
        null
    }

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
                    onSubmitClick = {
                        onSubmitClick(
                            account = account,
                            name = name,
                            body = body,
                            url = url,
                            postEditViewModel = postEditViewModel,
                            isNsfw = isNsfw,
                            navController = navController,
                            postViewModel = postViewModel,
                            communityViewModel = communityViewModel,
                            personProfileViewModel = personProfileViewModel,
                            homeViewModel = homeViewModel,
                        )
                    },
                    submitIcon = {
                        EditPostSubmitIcon()
                    },
                    title = stringResource(R.string.post_edit_edit_post),

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
                onPickedImage = { uri ->
                    val imageIs = imageInputStreamFromUri(ctx, uri)
                    scope.launch {
                        account?.also { acct ->
                            url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                        }
                    }
                },
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
    postViewModel: PostViewModel,
    communityViewModel: CommunityViewModel,
    personProfileViewModel: PersonProfileViewModel,
    homeViewModel: HomeViewModel,
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
            navController = navController,
            postViewModel = postViewModel,
            personProfileViewModel = personProfileViewModel,
            communityViewModel = communityViewModel,
            homeViewModel = homeViewModel,
        )
    }
}
