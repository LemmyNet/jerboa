package com.jerboa.ui.components.post.create

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.getKotlinxSerializerSaver
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CreatePostViewModel
import com.jerboa.padUrlWithHttps
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListReturn
import com.jerboa.ui.components.post.composables.CreateEditPostBody
import com.jerboa.ui.components.post.composables.PostCommunitySelector
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.CreatePost
import it.vercruysse.lemmyapi.datatypes.GetSiteMetadata
import it.vercruysse.lemmyapi.datatypes.PostId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchSiteMetadataJob: Job? = null

data class MetaDataRes(
    val title: String?,
    val loading: Boolean,
)

object CreatePostReturn {
    const val COMMUNITY_SEND = "create-post::send(community)"
}

@Composable
fun CreatePostScreen(
    accountViewModel: AccountViewModel,
    appState: JerboaAppState,
    initialUrl: String,
    initialBody: String,
    initialImage: Uri?,
) {
    Log.d("jerboa", "got to create post screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    val createPostViewModel: CreatePostViewModel = viewModel()

    var selectedCommunity by rememberSaveable(stateSaver = getKotlinxSerializerSaver<Community?>()) {
        // Init return from Community post creation
        mutableStateOf(appState.getPrevReturnNullable<Community?>(CreatePostReturn.COMMUNITY_SEND))
    }

    // On return from the community picker
    appState.ConsumeReturn<Community>(CommunityListReturn.COMMUNITY) { community ->
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
    var isUploadingCustomThumbnailImage by rememberSaveable { mutableStateOf(false) }
    var altText by rememberSaveable { mutableStateOf("") }
    var customThumbnail by rememberSaveable { mutableStateOf("") }

    val nameField = validatePostName(ctx, name)
    val urlField = validateUrl(ctx, url)
    val customThumbnailField = validateUrl(ctx, customThumbnail, ctx.getString(R.string.custom_thumbnail))
    val formValid = !nameField.hasError && !urlField.hasError && !customThumbnailField.hasError && (selectedCommunity !== null)

    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotEmpty()) {
            fetchSiteMetadataJob?.cancel()
            fetchSiteMetadataJob =
                scope.launch {
                    delay(DEBOUNCE_DELAY)
                    if (Patterns.WEB_URL.matcher(initialUrl).matches()) {
                        createPostViewModel.getSiteMetadata(GetSiteMetadata(initialUrl))
                    }
                }
        }
    }

    val (suggestedTitle, suggestedTitleLoading) =
        // If its a torrent magnet link, use the dn param to extract the torrent title
        if (url.startsWith("magnet")) {
            val query = Uri.parse(url).query
            // Unfortunately you have to use a fake http uri, otherwise getQueryParameter throws a non-hierarchical error.
            val torrentDownloadName = Uri.parse("http://test.com?$query").getQueryParameter("dn")

            if (torrentDownloadName !== null) {
                MetaDataRes(torrentDownloadName, false)
            } else {
                MetaDataRes(null, false)
            }
        } else {
            when (val res = createPostViewModel.siteMetadataRes) {
                ApiState.Empty -> MetaDataRes(null, false)
                ApiState.Loading -> MetaDataRes(null, true)
                is ApiState.Success ->
                    MetaDataRes(res.data.metadata.title, false)
                else -> MetaDataRes(null, false)
            }
        }
    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                Column {
                    val loading =
                        when (createPostViewModel.createPostRes) {
                            ApiState.Loading -> true
                            else -> false
                        }
                    ActionTopBar(
                        formValid = formValid,
                        loading = loading,
                        onActionClick = {
                            if (!account.isAnon()) {
                                onSubmitClick(
                                    name = name,
                                    body = body,
                                    url = url,
                                    altText = altText,
                                    customThumbnail = customThumbnail,
                                    isNsfw = isNsfw,
                                    selectedCommunity = selectedCommunity,
                                    createPostViewModel = createPostViewModel,
                                    onSuccess = appState::toPostWithPopUpTo,
                                )
                            }
                        },
                        actionIcon =
                            if (formValid) {
                                Icons.AutoMirrored.Filled.Send
                            } else {
                                Icons.AutoMirrored.Outlined.Send
                            },
                        actionText = R.string.form_submit,
                        title = stringResource(R.string.create_post_create_post),
                        onBackClick = appState::popBackStack,
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
                        fetchSiteMetadataJob =
                            scope.launch {
                                delay(DEBOUNCE_DELAY)
                                if (Patterns.WEB_URL.matcher(url).matches()) {
                                    createPostViewModel.getSiteMetadata(GetSiteMetadata(url.padUrlWithHttps()))
                                }
                            }
                    },
                    sharedImage = initialImage,
                    isUploadingImage = isUploadingImage,
                    onImagePicked = { uri ->
                        if (!account.isAnon() && uri != Uri.EMPTY) {
                            val imageIs = imageInputStreamFromUri(ctx, uri)
                            scope.launch {
                                isUploadingImage = true
                                url = API.uploadPictrsImage(imageIs, ctx)
                                isUploadingImage = false
                            }
                        }
                    },
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
                    suggestedTitle = suggestedTitle,
                    suggestedTitleLoading = suggestedTitleLoading,
                    account = account,
                    padding = padding,
                    isNsfw = isNsfw,
                    onIsNsfwChange = { isNsfw = it },
                    communitySelector = {
                        PostCommunitySelector(
                            community = selectedCommunity,
                            onClickCommunityList = { appState.toCommunityList(select = true) },
                        )
                    },
                    error = when (val res = createPostViewModel.createPostRes) {
                        is ApiState.Failure -> res.msg
                        else -> null
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
    altText: String,
    customThumbnail: String,
    isNsfw: Boolean,
    selectedCommunity: Community?,
    createPostViewModel: CreatePostViewModel,
    onSuccess: (PostId) -> Unit,
) {
    selectedCommunity?.id?.also {
        // Clean up that data
        val nameOut = name.trim()
        val bodyOut = body.text.trim().ifEmpty { null }
        val urlOut = url.trim().ifEmpty { null }?.padUrlWithHttps()
        val altTextOut = altText.trim().ifEmpty { null }
        val customThumbnailOut = customThumbnail.trim().ifEmpty { null }
        createPostViewModel.createPost(
            CreatePost(
                name = nameOut,
                community_id = it,
                url = urlOut,
                alt_text = altTextOut,
                custom_thumbnail = customThumbnailOut,
                body = bodyOut,
                nsfw = isNsfw,
            ),
            onSuccess = onSuccess,
        )
    }
}
