package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.jerboa.JerboaAppState
import com.jerboa.PostLinkType
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.InstantScores
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.SwipeToActionType
import com.jerboa.feat.VoteType
import com.jerboa.feat.isReadyAndIfNotShowSimplifiedInfoToast
import com.jerboa.ui.components.common.EmbeddedDataLoader
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.SwipeToAction
import com.jerboa.ui.components.common.rememberSwipeActionState
import com.jerboa.ui.components.settings.about.TORRENT_HELP_LINK
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.LINK_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.POST_LINK_PIC_SIZE
import com.jerboa.ui.theme.Shapes
import com.jerboa.ui.theme.THUMBNAIL_CARET_SIZE
import com.jerboa.ui.theme.jerboaColorScheme
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.ImageDetails
import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.Post
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView

@ExperimentalLayoutApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListing(
    postView: PostView,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onHidePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewVotesClick: (PostId) -> Unit,
    showReply: Boolean = false,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    appState: JerboaAppState,
    showPostLinkPreview: Boolean,
    showIfRead: Boolean,
    voteDisplayMode: LocalUserVoteDisplayMode,
    postActionBarMode: PostActionBarMode,
    swipeToActionPreset: SwipeToActionPreset,
    disableVideoAutoplay: Boolean,
) {
    val ctx = LocalContext.current
    // This stores vote data
    var instantScores by remember {
        mutableStateOf(
            InstantScores(
                myVote = postView.my_vote,
                score = postView.counts.score,
                upvotes = postView.counts.upvotes,
                downvotes = postView.counts.downvotes,
            ),
        )
    }

    val upvoteClick = remember(postView, instantScores) {
        {
            instantScores = instantScores.update(VoteType.Upvote)
            onUpvoteClick(postView)
        }
    }

    val downvoteClick = remember(postView, instantScores) {
        {
            instantScores = instantScores.update(VoteType.Downvote)
            onDownvoteClick(postView)
        }
    }

    var viewSource by remember { mutableStateOf(false) }

    val swipeAction: (action: SwipeToActionType) -> Unit = remember(postView) {
        {
            if (account.isReadyAndIfNotShowSimplifiedInfoToast(ctx)) {
                when (it) {
                    SwipeToActionType.Upvote -> upvoteClick()
                    SwipeToActionType.Downvote -> downvoteClick()
                    SwipeToActionType.Reply -> onReplyClick(postView)
                    SwipeToActionType.Save -> onSaveClick(postView)
                }
            }
        }
    }

    val swipeState = rememberSwipeActionState(
        swipeToActionPreset = swipeToActionPreset,
        enableDownVotes = enableDownVotes,
        onAction = swipeAction,
        rememberKey = postView,
    )

    val swipeableContent: @Composable RowScope.() -> Unit = {
        Row {
            when (postViewMode) {
                PostViewMode.Card ->
                    PostListingCard(
                        postView = postView,
                        admins = admins,
                        moderators = moderators,
                        instantScores = instantScores,
                        onUpvoteClick = upvoteClick,
                        onDownvoteClick = downvoteClick,
                        onReplyClick = onReplyClick,
                        onPostClick = onPostClick,
                        onSaveClick = onSaveClick,
                        onCommunityClick = onCommunityClick,
                        onEditPostClick = onEditPostClick,
                        onDeletePostClick = onDeletePostClick,
                        onHidePostClick = onHidePostClick,
                        onReportClick = onReportClick,
                        onRemoveClick = onRemoveClick,
                        onBanPersonClick = onBanPersonClick,
                        onBanFromCommunityClick = onBanFromCommunityClick,
                        onLockPostClick = onLockPostClick,
                        onFeaturePostClick = onFeaturePostClick,
                        onViewVotesClick = onViewVotesClick,
                        onPersonClick = onPersonClick,
                        onViewSourceClick = {
                            viewSource = !viewSource
                        },
                        viewSource = viewSource,
                        showReply = showReply,
                        showCommunityName = showCommunityName,
                        fullBody = fullBody,
                        account = account,
                        expandedImage = true,
                        enableDownVotes = enableDownVotes,
                        showAvatar = showAvatar,
                        useCustomTabs = useCustomTabs,
                        usePrivateTabs = usePrivateTabs,
                        blurNSFW = blurNSFW,
                        showPostLinkPreview = showPostLinkPreview,
                        appState = appState,
                        showIfRead = showIfRead,
                        voteDisplayMode = voteDisplayMode,
                        postActionBarMode = postActionBarMode,
                        disableVideoAutoplay = disableVideoAutoplay,
                    )

                PostViewMode.SmallCard ->
                    PostListingCard(
                        postView = postView,
                        admins = admins,
                        moderators = moderators,
                        instantScores = instantScores,
                        onUpvoteClick = upvoteClick,
                        onDownvoteClick = downvoteClick,
                        onReplyClick = onReplyClick,
                        onPostClick = onPostClick,
                        onSaveClick = onSaveClick,
                        onCommunityClick = onCommunityClick,
                        onEditPostClick = onEditPostClick,
                        onDeletePostClick = onDeletePostClick,
                        onHidePostClick = onHidePostClick,
                        onReportClick = onReportClick,
                        onRemoveClick = onRemoveClick,
                        onBanPersonClick = onBanPersonClick,
                        onBanFromCommunityClick = onBanFromCommunityClick,
                        onLockPostClick = onLockPostClick,
                        onFeaturePostClick = onFeaturePostClick,
                        onViewVotesClick = onViewVotesClick,
                        onPersonClick = onPersonClick,
                        onViewSourceClick = {
                            viewSource = !viewSource
                        },
                        viewSource = viewSource,
                        showReply = showReply,
                        showCommunityName = showCommunityName,
                        fullBody = fullBody,
                        account = account,
                        expandedImage = false,
                        enableDownVotes = enableDownVotes,
                        showAvatar = showAvatar,
                        useCustomTabs = useCustomTabs,
                        usePrivateTabs = usePrivateTabs,
                        blurNSFW = blurNSFW,
                        showPostLinkPreview = showPostLinkPreview,
                        appState = appState,
                        showIfRead = showIfRead,
                        voteDisplayMode = voteDisplayMode,
                        postActionBarMode = postActionBarMode,
                        disableVideoAutoplay = disableVideoAutoplay,
                    )

                PostViewMode.List ->
                    PostListingList(
                        postView = postView,
                        instantScores = instantScores,
                        onUpvoteClick = upvoteClick,
                        onDownvoteClick = downvoteClick,
                        onPostClick = onPostClick,
                        showCommunityName = showCommunityName,
                        account = account,
                        showVotingArrowsInListView = showVotingArrowsInListView,
                        useCustomTabs = useCustomTabs,
                        usePrivateTabs = usePrivateTabs,
                        blurNSFW = blurNSFW,
                        appState = appState,
                        showIfRead = showIfRead,
                        enableDownVotes = enableDownVotes,
                        voteDisplayMode = voteDisplayMode,
                    )
            }
        }
    }

    if (swipeToActionPreset != SwipeToActionPreset.Disabled) {
        SwipeToAction(
            swipeToActionPreset = swipeToActionPreset,
            enableDownVotes = enableDownVotes,
            swipeableContent = swipeableContent,
            swipeState = swipeState,
        )
    } else {
        Row {
            swipeableContent()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThumbnailTile(
    post: Post,
    imageDetails: ImageDetails?,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurEnabled: Boolean,
    appState: JerboaAppState,
) {
    if (post.url == null) {
        return
    }

    EmbeddedDataLoader(post, imageDetails, {
        AsyncImage(
            model = null,
            contentDescription = null,
            placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_mono),
            error = rememberAsyncImagePainter(R.drawable.ic_launcher_mono),
            modifier = Modifier.size(POST_LINK_PIC_SIZE).clip(Shapes.large),
        )
    }) {
        if (it.isFailure) {
            Log.e("EmbeddedData", "Data failed loading", it.exceptionOrNull())
            return@EmbeddedDataLoader
        }
        val embeddedData = it.getOrThrow()
        val targetUrl = embeddedData.videoUrl ?: post.url ?: return@EmbeddedDataLoader
        val postLinkType = PostLinkType.fromURL(targetUrl)
        val thumbnailUrl = embeddedData.thumbnailUrl ?: if (postLinkType == PostLinkType.Image) post.url else null

        val postLinkPicMod = Modifier
            .size(POST_LINK_PIC_SIZE)
            .combinedClickable(
                onClick = {
                    if (postLinkType != PostLinkType.Link) {
                        appState.openMediaViewer(targetUrl, postLinkType)
                    } else {
                        appState.openLink(
                            targetUrl,
                            useCustomTabs,
                            usePrivateTabs,
                        )
                    }
                },
                onLongClick = {
                    appState.showLinkPopup(targetUrl)
                },
            )

        Box {
            if (thumbnailUrl != null) {
                PictrsThumbnailImage(
                    thumbnail = thumbnailUrl,
                    blur = blurEnabled,
                    roundBottomEndCorner = postLinkType != PostLinkType.Link,
                    contentDescription = post.alt_text,
                    modifier = postLinkPicMod,
                )
            } else {
                Card(
                    modifier = postLinkPicMod,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = null,
                            modifier = Modifier.size(LINK_ICON_SIZE),
                        )
                    }
                }
            }

            // Display a caret in the bottom right corner to denote this as an image/video
            if (postLinkType != PostLinkType.Link) {
                Icon(
                    painter = painterResource(id = R.drawable.triangle),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(THUMBNAIL_CARET_SIZE)
                            .align(Alignment.BottomEnd),
                    tint =
                        when (postLinkType) {
                            PostLinkType.Video -> MaterialTheme.jerboaColorScheme.videoHighlight
                            else -> MaterialTheme.jerboaColorScheme.imageHighlight
                        },
                )
            }
        }
    }
}

@Composable
fun MetadataCard(post: Post) {
    val embedTitle = post.embed_title
    // If there is a valid title or description show it
    if (embedTitle != null && (post.name != embedTitle || post.embed_description != null)) {
        OutlinedCard(
            shape = MaterialTheme.shapes.medium,
            modifier =
                Modifier
                    .padding(MEDIUM_PADDING)
                    .fillMaxWidth(),
            content = {
                Column(
                    modifier = Modifier.padding(MEDIUM_PADDING),
                ) {
                    if (post.name != embedTitle) {
                        Text(
                            text = embedTitle,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (post.embed_description != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
                        }
                    }
                    post.embed_description?.let {
                        // This is actually html, but markdown can render it
                        MyMarkdownText(
                            markdown = it,
                            onClick = {},
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun TorrentHelpInfo() {
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier =
            Modifier
                .padding(MEDIUM_PADDING)
                .fillMaxWidth(),
        content = {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING),
            ) {
                MyMarkdownText(
                    markdown = stringResource(R.string.torrent_help, TORRENT_HELP_LINK),
                    onClick = {},
                )
            }
        },
    )
}

@Composable
fun PostName(
    modifier: Modifier = Modifier,
    post: Post,
    read: Boolean,
    showIfRead: Boolean,
) {
    val color =
        if (showIfRead && read) {
            MaterialTheme.colorScheme.outline
        } else if (post.featured_local) {
            MaterialTheme.colorScheme.primary
        } else if (post.featured_community) {
            MaterialTheme.colorScheme.secondary
        } else {
            Color.Unspecified
        }

    Text(
        text = post.name,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        modifier = modifier.testTag("jerboa:posttitle"),
    )
}
