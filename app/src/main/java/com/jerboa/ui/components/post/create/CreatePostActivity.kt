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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.CreatePost
import com.jerboa.datatypes.types.GetSiteMetadata
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.ConsumeReturn
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListReturn
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.ui.components.post.composables.CreateEditPostHeader
import com.jerboa.ui.components.post.composables.CreatePostSubmitIcon
import com.jerboa.ui.components.post.composables.PostCommunitySelector
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchSiteMetadataJob: Job? = null

data class MetaDataRes(val title: String?, val loading: Boolean)

@Composable
fun CreatePostActivity(
    accountViewModel: AccountViewModel,
    navController: NavController,
    initialUrl: String,
    initialBody: String,
    initialImage: Uri?,
) {
    Log.d("jerboa", "got to create post activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    val createPostViewModel: CreatePostViewModel = viewModel()

    var selectedCommunity: Community? by remember { mutableStateOf(null) }
    navController.ConsumeReturn<Community>(CommunityListReturn.COMMUNITY) { community ->
        selectedCommunity = community
    }

    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf(initialUrl) }
    var isNsfw by rememberSaveable { mutableStateOf(false) }
    var body by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                initialBody,
            ),
        )
    }
    var isUploadingImage by rememberSaveable { mutableStateOf(false) }

    val nameField = validatePostName(name)
    val urlField = validateUrl(url)
    val formValid = !nameField.hasError && !urlField.hasError && (selectedCommunity !== null)

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
                Column {
                    val loading = when (createPostViewModel.createPostRes) {
                        ApiState.Loading -> true
                        else -> false
                    }
                    CreateEditPostHeader(
                        navController = navController,
                        formValid = formValid,
                        loading = loading,
                        onSubmitClick = {
                            onSubmitClick(
                                name = name,
                                body = body,
                                url = url,
                                isNsfw = isNsfw,
                                account = account,
                                createPostViewModel = createPostViewModel,
                                selectedCommunity = selectedCommunity,
                                navController = navController,
                            )
                        },
                        submitIcon = {
                            CreatePostSubmitIcon(formValid)
                        },
                        title = stringResource(R.string.create_post_create_post),
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
                    suggestedTitle = suggestedTitle,
                    suggestedTitleLoading = suggestedTitleLoading,
                    sharedImage = initialImage,
                    isUploadingImage = isUploadingImage,
                    onImagePicked = { uri ->
                        if (uri != Uri.EMPTY) {
                            val imageIs = imageInputStreamFromUri(ctx, uri)
                            scope.launch {
                                isUploadingImage = true
                                account?.also { acct ->
                                    url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                                }
                                isUploadingImage = false
                            }
                        }
                    },
                    account = account,
                    padding = padding,
                    isNsfw = isNsfw,
                    onIsNsfwChange = { isNsfw = it },
                    communitySelector = {
                        PostCommunitySelector(
                            community = selectedCommunity,
                            navController = navController,
                        )
                    },
                )
            },
        )
    }
}

fun onSubmitClick(
    name: String,
    body: TextFieldValue,
    url: String,
    isNsfw: Boolean,
    account: Account?,
    selectedCommunity: Community?,
    createPostViewModel: CreatePostViewModel,
    navController: NavController,
) {
    account?.also { acct ->
        selectedCommunity?.id?.also {
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
                    nsfw = isNsfw,
                ),
            ) { postId ->
                navController.navigate(
                    Route.PostArgs.makeRoute(id = "$postId"),
                ) {
                    popUpTo(Route.CREATE_POST) { inclusive = true }
                }
            }
        }
    }
}
