package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.JerboaAppState
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.datatypes.getAspectRatio
import com.jerboa.datatypes.sampleImagePostView
import com.jerboa.datatypes.sampleInstantScores
import com.jerboa.datatypes.sampleLinkNoThumbnailPostView
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.sampleMarkdownPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.InstantScores
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.VoteType
import com.jerboa.feat.amMod
import com.jerboa.feat.canMod
import com.jerboa.feat.default
import com.jerboa.feat.needBlur
import com.jerboa.feat.simulateModerators
import com.jerboa.hostNameCleaned
import com.jerboa.isSameInstance
import com.jerboa.rememberJerboaAppState
import com.jerboa.siFormat
import com.jerboa.toHttps
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.ActionBarButtonAndBadge
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.EmbeddedDataLoader
import com.jerboa.ui.components.common.EmbeddedVideoPlayer
import com.jerboa.ui.components.common.MarkdownHelper.CreateMarkdownPreview
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.common.UpvotePercentage
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.common.VoteScore
import com.jerboa.ui.components.common.fadingEdge
import com.jerboa.ui.components.common.ifNotNull
import com.jerboa.ui.components.community.CommunityName
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.components.post.composables.PostOptionsDropdown
import com.jerboa.ui.components.videoviewer.VideoHostComposer
import com.jerboa.ui.components.videoviewer.hosts.DirectFileVideoHost
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALLER_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.VERTICAL_SPACING
import com.jerboa.ui.theme.XXL_PADDING
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView
import kotlinx.coroutines.CoroutineScope

@ExperimentalLayoutApi
@Composable
fun PostListingCard(
    postView: PostView,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
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
    onViewSourceClick: () -> Unit,
    viewSource: Boolean,
    showReply: Boolean = false,
    fullBody: Boolean,
    account: Account,
    expandedImage: Boolean,
    enableDownVotes: Boolean,
    showCommunityName: Boolean = true,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: BlurNSFW,
    showPostLinkPreview: Boolean,
    appState: JerboaAppState,
    showIfRead: Boolean,
    voteDisplayMode: LocalUserVoteDisplayMode,
    postActionBarMode: PostActionBarMode,
    disableVideoAutoplay: Boolean,
) {
    Column(
        modifier =
            Modifier
                .padding(vertical = SMALL_PADDING)
                .clickable { onPostClick(postView) }
                .testTag("jerboa:post"),
        // see https://stackoverflow.com/questions/77010371/prevent-popup-from-adding-padding-in-a-column-with-arrangement-spacedbylarge-p
        // verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        //  Title + metadata + attribution + body
        PostTitleAttributionBody(
            postView = postView,
            fullBody = fullBody,
            viewSource = viewSource,
            expandedImage = expandedImage,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            showPostLinkPreview = showPostLinkPreview,
            appState = appState,
            clickBody = { onPostClick(postView) },
            showIfRead = showIfRead,
            blurNSFW = blurNSFW,
            onPersonClick = onPersonClick,
            onCommunityClick = onCommunityClick,
            showAvatar = showAvatar,
            showCommunityName = showCommunityName,
            disableVideoAutoplay = disableVideoAutoplay,
        )

        Spacer(modifier = Modifier.padding(vertical = VERTICAL_SPACING))

        // Footer bar
        PostFooterLine(
            postView = postView,
            admins = admins,
            moderators = moderators,
            instantScores = instantScores,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onReplyClick = onReplyClick,
            onSaveClick = onSaveClick,
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
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            onViewSourceClick = onViewSourceClick,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            showReply = showReply,
            account = account,
            enableDownVotes = enableDownVotes,
            viewSource = viewSource,
            postActionBarMode = postActionBarMode,
            fromPostActivity = fullBody,
            voteDisplayMode = voteDisplayMode,
            scope = appState.coroutineScope,
        )
    }
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewPostListingCard() {
    PostListingCard(
        postView = samplePostView,
        admins = emptyList(),
        moderators = emptyList(),
        useCustomTabs = false,
        usePrivateTabs = false,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        postActionBarMode = PostActionBarMode.Long,
        instantScores = sampleInstantScores,
        onViewSourceClick = {},
        viewSource = false,
        expandedImage = false,
        disableVideoAutoplay = false,
    )
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListingCard(
        postView = sampleLinkPostView,
        admins = emptyList(),
        moderators = emptyList(),
        useCustomTabs = false,
        usePrivateTabs = false,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        postActionBarMode = PostActionBarMode.Long,
        instantScores = sampleInstantScores,
        onViewSourceClick = {},
        viewSource = false,
        expandedImage = false,
        disableVideoAutoplay = false,
    )
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewImagePostListingCard() {
    PostListingCard(
        postView = sampleImagePostView,
        admins = emptyList(),
        moderators = emptyList(),
        useCustomTabs = false,
        usePrivateTabs = false,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        postActionBarMode = PostActionBarMode.Long,
        instantScores = sampleInstantScores,
        onViewSourceClick = {},
        viewSource = false,
        expandedImage = true,
        disableVideoAutoplay = false,
    )
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewImagePostListingSmallCard() {
    PostListingCard(
        postView = sampleImagePostView,
        admins = emptyList(),
        moderators = emptyList(),
        useCustomTabs = false,
        usePrivateTabs = false,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        postActionBarMode = PostActionBarMode.Long,
        instantScores = sampleInstantScores,
        onViewSourceClick = {},
        viewSource = false,
        expandedImage = false,
        disableVideoAutoplay = false,
    )
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListingCard(
        postView = sampleLinkNoThumbnailPostView,
        admins = emptyList(),
        moderators = emptyList(),
        useCustomTabs = false,
        usePrivateTabs = false,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        postActionBarMode = PostActionBarMode.Long,
        instantScores = sampleInstantScores,
        onViewSourceClick = {},
        viewSource = false,
        expandedImage = false,
        disableVideoAutoplay = false,
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    instantScores: InstantScores,
    voteDisplayMode: LocalUserVoteDisplayMode,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onHidePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
    onViewVotesClick: (PostId) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
    showReply: Boolean = false,
    account: Account,
    enableDownVotes: Boolean,
    viewSource: Boolean,
    postActionBarMode: PostActionBarMode,
    fromPostActivity: Boolean,
    scope: CoroutineScope,
) {
    val ctx = LocalContext.current
    var showMoreOptions by rememberSaveable { mutableStateOf(false) }

    if (showMoreOptions) {
        val fallbackModerators = remember(moderators) {
            moderators ?: simulateModerators(
                ctx = ctx,
                account = account,
                forCommunity = postView.community.id,
            )
        }

        val amMod = remember(moderators) {
            amMod(
                moderators = fallbackModerators,
                myId = account.id,
            )
        }

        val canMod = remember(admins, moderators) {
            canMod(
                creatorId = postView.creator.id,
                admins = admins,
                moderators = fallbackModerators,
                myId = account.id,
            )
        }

        PostOptionsDropdown(
            postView = postView,
            onDismissRequest = { showMoreOptions = false },
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
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
            onViewSourceClick = onViewSourceClick,
            isCreator = account.id == postView.creator.id,
            canMod = canMod,
            amAdmin = account.isAdmin,
            amMod = amMod,
            viewSource = viewSource,
            showViewSource = fromPostActivity,
            scope = scope,
        )
    }

    val horizontalArrangement =
        when (postActionBarMode) {
            PostActionBarMode.Long -> Arrangement.spacedBy(XXL_PADDING)
            PostActionBarMode.LeftHandShort -> Arrangement.spacedBy(LARGE_PADDING)
            PostActionBarMode.RightHandShort -> Arrangement.spacedBy(LARGE_PADDING)
        }

    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = SMALL_PADDING),
    ) {
        // Right handside shows the comments on the left side
        if (postActionBarMode == PostActionBarMode.RightHandShort) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
                modifier = Modifier.weight(1F, true),
            )
        }
        VoteScore(
            instantScores = instantScores,
            onVoteClick = onUpvoteClick,
            voteDisplayMode = voteDisplayMode,
            account = account,
        )
        UpvotePercentage(
            instantScores = instantScores,
            voteDisplayMode = voteDisplayMode,
            account = account,
        )
        VoteGeneric(
            instantScores = instantScores,
            type = VoteType.Upvote,
            onVoteClick = onUpvoteClick,
            voteDisplayMode = voteDisplayMode,
            account = account,
        )
        if (enableDownVotes) {
            VoteGeneric(
                instantScores = instantScores,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                voteDisplayMode = voteDisplayMode,
                account = account,
            )
        }

        if (postActionBarMode == PostActionBarMode.Long) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
                modifier = Modifier.weight(1F, true),
            )
        }

        if (showReply) {
            ActionBarButton(
                icon = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = stringResource(R.string.postListing_reply),
                onClick = { onReplyClick(postView) },
                account = account,
            )
        }
        SavedButton(
            saved = postView.saved,
            account = account,
            onSaveClick = { onSaveClick(postView) },
        )
        ActionBarButton(
            icon = Icons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.moreOptions),
            account = account,
            onClick = { showMoreOptions = !showMoreOptions },
            requiresAccount = false,
            modifier = if (postActionBarMode == PostActionBarMode.LeftHandShort) {
                Modifier.weight(
                    1F,
                    true,
                )
            } else {
                Modifier
            },
        )

        if (postActionBarMode == PostActionBarMode.LeftHandShort) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
            )
        }
    }
}

@Preview
@Composable
fun PostFooterLinePreview() {
    PostFooterLine(
        postView = samplePostView,
        admins = emptyList(),
        moderators = emptyList(),
        instantScores = sampleInstantScores,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewVotesClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onViewSourceClick = {},
        account = AnonAccount,
        enableDownVotes = true,
        viewSource = false,
        postActionBarMode = PostActionBarMode.Long,
        fromPostActivity = true,
        scope = rememberCoroutineScope(),
    )
}

@ExperimentalLayoutApi
@Composable
fun PostCommunityAndCreatorBlock(
    postView: PostView,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    modifier: Modifier = Modifier,
    showCommunityName: Boolean = true,
    showAvatar: Boolean,
    fullBody: Boolean,
    blurNSFW: BlurNSFW,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        val centerMod = Modifier.align(Alignment.CenterVertically)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SMALLER_PADDING, Alignment.Start),
        ) {
            if (showCommunityName && showAvatar) {
                CommunityIcon(
                    modifier = centerMod.padding(end = SMALL_PADDING),
                    community = postView.community,
                    onCommunityClick = onCommunityClick,
                    blurNSFW = blurNSFW,
                )
            }
            if (showCommunityName) {
                CommunityName(
                    community = postView.community,
                    modifier = centerMod,
                    onClick = { onCommunityClick(postView.community) },
                )
                Text(
                    text = stringResource(R.string.by),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = centerMod,
                )
            }
            PersonProfileLink(
                person = postView.creator,
                onClick = onPersonClick,
                showTags = fullBody,
                // Set this to false, we already know this
                isPostCreator = false,
                isCommunityBanned = postView.creator_banned_from_community,
                color = MaterialTheme.colorScheme.outline,
                showAvatar = !showCommunityName && showAvatar,
                modifier = centerMod,
            )
            if (postView.post.featured_local) {
                DotSpacer(modifier = centerMod)
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = stringResource(R.string.postListing_featuredLocal),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = centerMod.size(ACTION_BAR_ICON_SIZE),
                )
            }
            if (postView.post.featured_community) {
                DotSpacer(modifier = centerMod)
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = stringResource(R.string.postListing_featuredCommunity),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = centerMod.size(ACTION_BAR_ICON_SIZE),
                )
            }
            if (postView.post.locked) {
                DotSpacer(modifier = centerMod)
                Icon(
                    imageVector = Icons.Outlined.CommentsDisabled,
                    contentDescription = stringResource(R.string.locked),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = centerMod.size(ACTION_BAR_ICON_SIZE),
                )
            }
            if (postView.post.deleted) {
                DotSpacer(modifier = centerMod)
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.deleted),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = centerMod,
                )
            }
            if (postView.post.removed) {
                DotSpacer(modifier = centerMod)
                Icon(
                    imageVector = Icons.Outlined.Gavel,
                    contentDescription = stringResource(R.string.removed),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = centerMod,
                )
            }
        }
        TimeAgo(
            published = postView.post.published,
            updated = postView.post.updated,
            modifier = centerMod,
        )
    }
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PostCommunityAndCreatorPreview() {
    val postView = sampleLinkPostView
    PostCommunityAndCreatorBlock(
        postView = postView,
        onCommunityClick = {},
        onPersonClick = {},
        showAvatar = true,
        blurNSFW = BlurNSFW.NSFW,
        fullBody = true,
    )
}

@Composable
fun CommunityIcon(
    modifier: Modifier = Modifier,
    community: Community,
    onCommunityClick: (community: Community) -> Unit,
    blurNSFW: BlurNSFW,
) {
    community.icon?.let {
        CircularIcon(
            icon = it,
            contentDescription = stringResource(R.string.postListing_goToCommunity),
            modifier = modifier.clickable { onCommunityClick(community) },
            blur = blurNSFW.needBlur(community.nsfw),
        )
    }
}

@ExperimentalLayoutApi
@Composable
fun PostTitleAttributionBody(
    postView: PostView,
    fullBody: Boolean,
    viewSource: Boolean,
    expandedImage: Boolean,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    showPostLinkPreview: Boolean,
    appState: JerboaAppState,
    clickBody: () -> Unit = {},
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    showIfRead: Boolean,
    blurNSFW: BlurNSFW,
    showCommunityName: Boolean,
    showAvatar: Boolean,
    disableVideoAutoplay: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING),
    ) {
        PostCommunityAndCreatorBlock(
            postView = postView,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            showCommunityName = showCommunityName,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            showAvatar = showAvatar,
            fullBody = fullBody,
            blurNSFW = blurNSFW,
        )

        PostTitleBlock(
            postView = postView,
            expandedImage = expandedImage,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            appState = appState,
            showIfRead = showIfRead,
            blurNSFW = blurNSFW,
            disableVideoAutoplay = disableVideoAutoplay,
        )

        // The metadata card
        if (fullBody && showPostLinkPreview) {
            MetadataCard(post = postView.post)

            if (postView.post.url?.startsWith("magnet") == true) {
                TorrentHelpInfo()
            }
        }

        PostBody(
            body = postView.post.body,
            fullBody = fullBody,
            viewSource = viewSource,
            clickBody = clickBody,
        )
    }
}

@Composable
fun PostBody(
    body: String?,
    fullBody: Boolean,
    viewSource: Boolean,
    clickBody: () -> Unit,
) {
    // Check to make sure body isn't empty string
    val bodyTrimmed = body
        ?.trim()
        ?.ifEmpty { null }

    // The desc
    bodyTrimmed?.also { text ->
        if (fullBody) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = MEDIUM_PADDING),
            ) {
                if (viewSource) {
                    SelectionContainer {
                        Text(
                            text = text,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                } else {
                    MyMarkdownText(
                        markdown = text,
                        onClick = {},
                    )
                }
            }
        } else {
            val bottomFade = Brush.verticalGradient(.7f to MaterialTheme.colorScheme.background, 1f to Color.Transparent)
            CreateMarkdownPreview(
                markdown = text,
                color = LocalContentColor.current,
                onClick = clickBody,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = MEDIUM_PADDING)
                    .fadingEdge(bottomFade),
            )
        }
    }
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewStoryTitleAndMetadata() {
    PostTitleAttributionBody(
        postView = samplePostView,
        fullBody = false,
        viewSource = false,
        expandedImage = false,
        account = AnonAccount,
        useCustomTabs = false,
        usePrivateTabs = false,
        showPostLinkPreview = true,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        blurNSFW = BlurNSFW.NSFW,
        onPersonClick = {},
        onCommunityClick = {},
        showAvatar = true,
        showCommunityName = true,
        disableVideoAutoplay = false,
    )
}

@ExperimentalLayoutApi
@Preview
@Composable
fun PreviewSourcePost() {
    val pv = sampleMarkdownPostView
    PostTitleAttributionBody(
        postView = pv,
        fullBody = true,
        viewSource = true,
        expandedImage = false,
        account = AnonAccount,
        useCustomTabs = false,
        usePrivateTabs = false,
        showPostLinkPreview = true,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        blurNSFW = BlurNSFW.NSFW,
        onPersonClick = {},
        onCommunityClick = {},
        showAvatar = true,
        showCommunityName = true,
        disableVideoAutoplay = false,
    )
}

@Composable
fun SavedButton(
    saved: Boolean,
    account: Account,
    onSaveClick: () -> Unit,
) {
    ActionBarButton(
        icon = if (saved) {
            Icons.Filled.Bookmark
        } else {
            Icons.Outlined.BookmarkBorder
        },
        contentDescription = if (saved) {
            stringResource(R.string.removeBookmark)
        } else {
            stringResource(R.string.addBookmark)
        },
        onClick = onSaveClick,
        contentColor = if (saved) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        account = account,
    )
}

@ExperimentalLayoutApi
@Composable
fun PostTitleBlock(
    postView: PostView,
    expandedImage: Boolean,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    appState: JerboaAppState,
    showIfRead: Boolean,
    blurNSFW: BlurNSFW,
    disableVideoAutoplay: Boolean,
) {
    val postUrl = postView.post.url
    val postType = postUrl?.let { PostLinkType.fromURL(it) }
    val imagePost = postType == PostLinkType.Image
    // Also support hosts that we don't manually support (Through OGP), but they must link directly to a playable link.
    val videoPost = postType == PostLinkType.Video ||
        DirectFileVideoHost.isDirectUrl(postView.post.embed_video_url) ||
        (postUrl != null && VideoHostComposer.isVideo(postUrl))

    when {
        videoPost && expandedImage -> {
            PostTitleAndVideoLink(
                postView = postView,
                appState = appState,
                showIfRead = showIfRead,
                blurNSFW = blurNSFW,
                disableVideoAutoplay = disableVideoAutoplay,
            )
        }

        imagePost && expandedImage -> {
            PostTitleAndImageLink(
                postView = postView,
                appState = appState,
                showIfRead = showIfRead,
                blurNSFW = blurNSFW,
            )
        }

        else -> {
            PostTitleAndThumbnail(
                postView = postView,
                account = account,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                appState = appState,
                showIfRead = showIfRead,
                blurNSFW = blurNSFW,
            )
        }
    }
}

@Composable
fun PostTitleAndImageLink(
    postView: PostView,
    appState: JerboaAppState,
    showIfRead: Boolean,
    blurNSFW: BlurNSFW,
) {
    // This was tested, we know it exists
    val url = postView.post.url?.toHttps()

    // Title of the post
    PostName(
        post = postView.post,
        read = postView.read,
        showIfRead = showIfRead,
        modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
    )

    url?.let { cUrl ->
        PictrsUrlImage(
            url = cUrl,
            blur = blurNSFW.needBlur(postView),
            contentDescription = postView.post.alt_text,
            modifier = Modifier
                .ifNotNull(postView.image_details?.getAspectRatio()) {
                    aspectRatio(it)
                }.combinedClickable(
                    onClick = { appState.openImageViewer(cUrl) },
                    onLongClick = { appState.showLinkPopup(cUrl) },
                ),
        )
    }
}

@Composable
fun PostTitleAndVideoLink(
    postView: PostView,
    appState: JerboaAppState,
    blurNSFW: BlurNSFW,
    showIfRead: Boolean,
    disableVideoAutoplay: Boolean,
) {
    // Title of the post
    PostName(
        post = postView.post,
        read = postView.read,
        showIfRead = showIfRead,
        modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
    )

    EmbeddedDataLoader(postView.post, postView.image_details) {
        if (it.isFailure) {
            Text(text = "Failed to load video data")
            ApiErrorText(it.exceptionOrNull()!!)
            Log.e("EmbeddedVideoPlayer", "Failed to load video data", it.exceptionOrNull())
        } else {
            val videoData = it.getOrThrow()
            if (videoData.videoUrl != null) {
                EmbeddedVideoPlayer(
                    id = postView.post.id,
                    thumbnailUrl = videoData.thumbnailUrl,
                    videoUrl = videoData.videoUrl,
                    title = videoData.title,
                    // Need fixed aspect ratio for videos, to prevent feed hopping
                    aspectRatio = videoData.aspectRatio ?: (16F / 9F),
                    hostId = videoData.typeName,
                    blur = blurNSFW.needBlur(postView),
                    disableVideoAutoplay = disableVideoAutoplay,
                    appState = appState,
                )
            }
        }
    }
}

@Composable
fun PostTitleAndThumbnail(
    postView: PostView,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    appState: JerboaAppState,
    showIfRead: Boolean,
    blurNSFW: BlurNSFW,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
    ) {
        // Title of the post
        Column(
            verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING),
            modifier = Modifier.weight(1f),
        ) {
            PostName(
                post = postView.post,
                read = postView.read,
                showIfRead = showIfRead,
            )
            HostnameSimplified(postView.post.url, account)
        }
        ThumbnailTile(
            post = postView.post,
            imageDetails = postView.image_details,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurEnabled = blurNSFW.needBlur(postView),
            appState = appState,
        )
    }
}

@Composable
fun HostnameSimplified(
    url: String?,
    account: Account,
) {
    url?.also { postUrl ->
        if (!isSameInstance(postUrl, account.instance)) {
            val hostName = hostNameCleaned(postUrl)
            hostName?.also {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
fun CommentNewCountRework(
    comments: Long,
    unreadCount: Long,
    account: Account,
    modifier: Modifier = Modifier,
) {
    val unread =
        if (unreadCount == 0L || comments == unreadCount) {
            null
        } else {
            (if (unreadCount > 0) "+" else "") + siFormat(unreadCount)
        }

    ActionBarButtonAndBadge(
        icon = Icons.Outlined.ChatBubbleOutline,
        iconBadgeCount = unread,
        contentDescription = null,
        text = siFormat(comments),
        noClick = true,
        account = account,
        onClick = {},
        modifier = modifier,
    )
}

@Preview
@Composable
fun CommentCountPreview() {
    CommentNewCountRework(42, 0, account = AnonAccount)
}
