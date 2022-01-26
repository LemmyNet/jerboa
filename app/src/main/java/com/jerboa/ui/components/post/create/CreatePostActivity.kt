package com.jerboa.ui.components.post.create

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.api.uploadPictrsImage
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun CreatePostActivity(
    accountViewModel: AccountViewModel,
    createPostViewModel: CreatePostViewModel,
    navController: NavController,
    communityListViewModel: CommunityListViewModel,
    postViewModel: PostViewModel,
) {

    Log.d("jerboa", "got to create post activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var formValid by rememberSaveable { mutableStateOf(false) }

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                Column {
                    CreatePostHeader(
                        navController = navController,
                        formValid = formValid,
                        loading = createPostViewModel.loading,
                        onCreatePostClick = {
                            account?.also { acct ->
                                communityListViewModel.selectedCommunity?.id?.also {
                                    // Clean up that data
                                    val nameOut = name.trim()
                                    val bodyOut = body.trim().ifEmpty { null }
                                    val urlOut = url.trim().ifEmpty { null }
                                    createPostViewModel.createPost(
                                        account = acct,
                                        ctx = ctx,
                                        body = bodyOut,
                                        url = urlOut,
                                        name = nameOut,
                                        communityId = it,
                                        navController = navController,
                                        postViewModel = postViewModel,
                                    )
                                }
                            }
                        }
                    )
                    if (createPostViewModel.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = {
                CreatePostBody(
                    name = name,
                    onNameChange = { name = it },
                    body = body,
                    onBodyChange = { body = it },
                    url = url,
                    onUrlChange = { cUrl ->
                        url = cUrl
                        if (Patterns.WEB_URL.matcher(cUrl).matches()) {
                            createPostViewModel.fetchSuggestedTitle(cUrl)
                        }
                    },
                    navController = navController,
                    community = communityListViewModel.selectedCommunity,
                    formValid = { formValid = it },
                    suggestedTitle = createPostViewModel.suggestedTitle,
                    onPickedImage = { uri ->
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            account?.also { acct ->
                                url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                            }
                        }
                    }
                )
            }
        )
    }
}
