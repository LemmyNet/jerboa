package com.jerboa.ui.components.post.create

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.CreatePost
import com.jerboa.datatypes.types.GetSiteMetadata
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchSiteMetadataJob: Job? = null

data class MetaDataRes(val title: String?, val loading: Boolean)

@Composable
fun CreatePostActivity(
    accountViewModel: AccountViewModel,
    createPostViewModel: CreatePostViewModel,
    navController: NavController,
    communityListViewModel: CommunityListViewModel,
    initialUrl: String,
    initialBody: String,
    initialImage: Uri?,
) {
    Log.d("jerboa", "got to create post activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf(initialUrl) }
    var body by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                initialBody,
            ),
        )
    }
    var formValid by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotEmpty()) {
            fetchSiteMetadataJob?.cancel()
            fetchSiteMetadataJob = scope.launch {
                delay(DEBOUNCE_DELAY)
                if (Patterns.WEB_URL.matcher(initialUrl).matches()) {
                    createPostViewModel.getSiteMetadata(GetSiteMetadata(initialUrl))
                }
            }
        }
    }

    val (suggestedTitle, suggestedTitleLoading) = when (val res = createPostViewModel.siteMetadataRes) {
        ApiState.Empty -> MetaDataRes(null, false)
        ApiState.Loading -> MetaDataRes(null, true)
        is ApiState.Success ->
            MetaDataRes(res.data.metadata.title, false)
        else -> MetaDataRes(null, false)
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                val loading = when (createPostViewModel.createPostRes) {
                    ApiState.Loading -> true
                    else -> false
                }
                Column {
                    CreatePostHeader(
                        navController = navController,
                        formValid = formValid,
                        loading = loading,
                        onCreatePostClick = {
                            account?.also { acct ->
                                communityListViewModel.selectedCommunity?.id?.also {
                                    // Clean up that data
                                    val nameOut = name.trim()
                                    val bodyOut = body.text.trim().ifEmpty { null }
                                    val urlOut = url.trim().ifEmpty { null }
                                    createPostViewModel.createPost(
                                        CreatePost(
                                            name = nameOut,
                                            community_id = it,
                                            url = urlOut,
                                            body = bodyOut,
                                            auth = acct.jwt,
                                        ),
                                        navController,
                                    )
                                }
                            }
                        },
                    )
                    if (loading) {
                        LoadingBar()
                    }
                }
            },
            content = { padding ->
                CreatePostBody(
                    name = name,
                    onNameChange = { name = it },
                    body = body,
                    onBodyChange = { body = it },
                    url = url,
                    onUrlChange = { cUrl ->
                        url = cUrl
                        fetchSiteMetadataJob?.cancel()
                        fetchSiteMetadataJob = scope.launch {
                            delay(DEBOUNCE_DELAY)
                            if (Patterns.WEB_URL.matcher(cUrl).matches()) {
                                createPostViewModel.getSiteMetadata(GetSiteMetadata(cUrl))
                            }
                        }
                    },
                    navController = navController,
                    community = communityListViewModel.selectedCommunity,
                    formValid = { formValid = it },
                    suggestedTitle = suggestedTitle,
                    suggestedTitleLoading = suggestedTitleLoading,
                    image = initialImage,
                    onPickedImage = { uri ->
                        if (uri != Uri.EMPTY) {
                            val imageIs = imageInputStreamFromUri(ctx, uri)
                            scope.launch {
                                account?.also { acct ->
                                    url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                                }
                            }
                        }
                    },
                    account = account,
                    padding = padding,
                )
            },
        )
    }
}
