package com.jerboa.ui.components.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerboa.InstantScores
import com.jerboa.JerboaAppState
import com.jerboa.PostType
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.calculateNewInstantScores
import com.jerboa.canMod
import com.jerboa.datatypes.sampleImagePostView
import com.jerboa.datatypes.sampleLinkNoThumbnailPostView
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.sampleMarkdownPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.BlurTypes
import com.jerboa.feat.PostActionbarMode
import com.jerboa.feat.needBlur
import com.jerboa.getPostType
import com.jerboa.hostName
import com.jerboa.isSameInstance
import com.jerboa.nsfwCheck
import com.jerboa.rememberJerboaAppState
import com.jerboa.siFormat
import com.jerboa.toEnum
import com.jerboa.toHttps
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.ActionBarButtonAndBadge
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.MarkdownHelper.CreateMarkdownPreview
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.NsfwBadge
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.ScoreAndTime
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.common.scoreColor
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.community.CommunityName
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.components.post.composables.PostOptionsDropdown
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.CARD_COLORS
import com.jerboa.ui.theme.LARGER_ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.LINK_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.POST_LINK_PIC_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.THUMBNAIL_CARET_SIZE
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.jerboaColorScheme
import com.jerboa.ui.theme.muted
import it.vercruysse.lemmyapi.v0x19.datatypes.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope

@Composable
fun PostHeaderLine(
    postView: PostView,
    myVote: Int?,
    score: Int,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    modifier: Modifier = Modifier,
    showCommunityName: Boolean = true,
    showAvatar: Boolean,
    blurNSFW: Int,
    showScores: Boolean,
) {
    val community = postView.community
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(LARGE_PADDING),
                modifier = Modifier.weight(1f),
            ) {
                if (showCommunityName) {
                    community.icon?.let {
                        CircularIcon(
                            icon = it,
                            contentDescription = stringResource(R.string.postListing_goToCommunity),
                            size = MEDIUM_ICON_SIZE,
                            modifier = Modifier.clickable { onCommunityClick(community) },
                            thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
                            blur = blurNSFW.toEnum<BlurTypes>().needBlur(community.nsfw),
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)) {
                    if (showCommunityName) {
                        CommunityName(
                            community = postView.community,
                            modifier = Modifier.clickable { onCommunityClick(community) },
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                    ) {
                        PersonProfileLink(
                            person = postView.creator,
                            onClick = onPersonClick,
                            showTags = true,
                            isPostCreator = false, // Set this to false, we already know this
                            isModerator = postView.creator_is_moderator,
                            isAdmin = postView.creator_is_admin,
                            isCommunityBanned = postView.creator_banned_from_community,
                            color = MaterialTheme.colorScheme.onSurface.muted,
                            showAvatar = showAvatar,
                        )
                        if (postView.post.featured_local) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = stringResource(R.string.postListing_featuredLocal),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE),
                            )
                        }
                        if (postView.post.featured_community) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = stringResource(R.string.postListing_featuredCommunity),
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE),
                            )
                        }
                        if (postView.post.locked) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.CommentsDisabled,
                                contentDescription = stringResource(R.string.postListing_locked),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE),
                            )
                        }
                    }
                }
                if (postView.post.deleted) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.postListing_deleted),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
                if (postView.post.removed) {
                    Icon(
                        imageVector = Icons.Outlined.Gavel,
                        contentDescription = stringResource(R.string.removed),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            ScoreAndTime(
                score = score,
                myVote = myVote,
                published = postView.post.published,
                updated = postView.post.updated,
                isNsfw = nsfwCheck(postView),
                showScores = showScores,
            )
        }
    }
}

@Preview
@Composable
fun PostHeaderLinePreview() {
    val postView = sampleLinkPostView
    PostHeaderLine(
        postView = postView,
        myVote = null,
        score = 10,
        onCommunityClick = {},
        onPersonClick = {},
        showAvatar = true,
        blurNSFW = 1,
        showScores = true,
    )
}

@Composable
fun PostNodeHeader(
    postView: PostView,
    myVote: Int?,
    score: Int,
    onPersonClick: (personId: Int) -> Unit,
    showAvatar: Boolean,
    showScores: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = postView.creator,
        myVote = myVote,
        score = score,
        published = postView.post.published,
        updated = postView.post.updated,
        deleted = postView.post.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = true,
        isCommunityBanned = postView.creator_banned_from_community,
        onClick = {},
        onLongCLick = {},
        showAvatar = showAvatar,
        showScores = showScores,
        isModerator = postView.creator_is_moderator,
        isAdmin = postView.creator_is_admin,
    )
}

@Composable
fun PostTitleBlock(
    postView: PostView,
    expandedImage: Boolean,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    appState: JerboaAppState,
    showIfRead: Boolean,
) {
    val imagePost = postView.post.url?.let { getPostType(it) == PostType.Image } ?: false

    if (imagePost && expandedImage) {
        PostTitleAndImageLink(
            postView = postView,
            blurNSFW = blurNSFW,
            appState = appState,
            showIfRead = showIfRead,
        )
    } else {
        PostTitleAndThumbnail(
            postView = postView,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            appState = appState,
            showIfRead = showIfRead,
        )
    }
}

@Composable
fun PostName(
    postView: PostView,
    showIfRead: Boolean,
) {
    var color =
        if (postView.post.featured_local) {
            MaterialTheme.colorScheme.primary
        } else if (postView.post.featured_community) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.onSurface
        }

    if (showIfRead && postView.read) {
        color = color.muted
    }

    Text(
        text = postView.post.name,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        modifier = Modifier.testTag("jerboa:posttitle"),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostTitleAndImageLink(
    postView: PostView,
    blurNSFW: Int,
    appState: JerboaAppState,
    showIfRead: Boolean,
) {
    // This was tested, we know it exists
    val url = postView.post.url!!.toHttps()

    Column(
        modifier =
            Modifier.padding(
                vertical = MEDIUM_PADDING,
                horizontal = MEDIUM_PADDING,
            ),
    ) {
        // Title of the post
        PostName(
            postView = postView,
            showIfRead = showIfRead,
        )
    }

    PictrsUrlImage(
        url = url,
        blur = blurNSFW.toEnum<BlurTypes>().needBlur(postView),
        modifier =
            Modifier
                .combinedClickable(
                    onClick = { appState.openImageViewer(url) },
                    onLongClick = { appState.showLinkPopup(url) },
                ),
    )
}

@Composable
fun PostTitleAndThumbnail(
    postView: PostView,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    appState: JerboaAppState,
    showIfRead: Boolean,
) {
    Column(
        modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        ) {
            // Title of the post
            Column(
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
                modifier = Modifier.weight(1f),
            ) {
                PostName(postView = postView, showIfRead = showIfRead)
                postView.post.url?.also { postUrl ->
                    if (!isSameInstance(postUrl, account.instance)) {
                        val hostName = hostName(postUrl)
                        hostName?.also {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onBackground.muted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            ThumbnailTile(
                postView = postView,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                appState = appState,
            )
        }
    }
}

@Composable
fun PostBody(
    postView: PostView,
    fullBody: Boolean,
    viewSource: Boolean,
    expandedImage: Boolean,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    showPostLinkPreview: Boolean,
    appState: JerboaAppState,
    clickBody: () -> Unit = {},
    showIfRead: Boolean,
) {
    val post = postView.post
    Column(
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        PostTitleBlock(
            postView = postView,
            expandedImage = expandedImage,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            appState = appState,
            showIfRead = showIfRead,
        )

        // The metadata card
        if (fullBody && showPostLinkPreview && post.embed_title !== null) {
            MetadataCard(post = post)
        }

        // Check to make sure body isn't empty string
        val body = post.body?.trim()?.ifEmpty { null }

        // The desc
        body?.also { text ->
            Card(
                colors = CARD_COLORS,
                shape = MaterialTheme.shapes.medium,
                modifier =
                    Modifier
                        .padding(vertical = MEDIUM_PADDING, horizontal = MEDIUM_PADDING)
                        .fillMaxWidth(),
                content = {
                    if (fullBody) {
                        Column(
                            modifier =
                                Modifier
                                    .padding(MEDIUM_PADDING),
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
                        val defaultColor: Color =
                            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)

                        CreateMarkdownPreview(
                            markdown = text,
                            defaultColor = defaultColor,
                            onClick = clickBody,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(MEDIUM_PADDING),
                        )
                    }
                },
            )
        }
    }
}

@Preview
@Composable
fun PreviewStoryTitleAndMetadata() {
    PostBody(
        postView = samplePostView,
        fullBody = false,
        viewSource = false,
        expandedImage = false,
        account = AnonAccount,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = 1,
        showPostLinkPreview = true,
        appState = rememberJerboaAppState(),
        showIfRead = true,
    )
}

@Preview
@Composable
fun PreviewSourcePost() {
    PostBody(
        postView = sampleMarkdownPostView,
        fullBody = true,
        viewSource = true,
        expandedImage = false,
        account = AnonAccount,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = 1,
        showPostLinkPreview = true,
        appState = rememberJerboaAppState(),
        showIfRead = true,
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    admins: ImmutableList<PersonView>,
    moderators: ImmutableList<CommunityModeratorView>?,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onViewSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
    showReply: Boolean = false,
    account: Account,
    enableDownVotes: Boolean,
    viewSource: Boolean,
    showScores: Boolean,
    postActionbarMode: Int,
    fromPostActivity: Boolean,
    scope: CoroutineScope,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    val canMod =
        remember {
            canMod(
                creatorId = postView.creator.id,
                admins = admins,
                moderators = moderators,
                myId = account.id,
            )
        }

    if (showMoreOptions) {
        PostOptionsDropdown(
            postView = postView,
            onDismissRequest = { showMoreOptions = false },
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            onEditPostClick = onEditPostClick,
            onDeletePostClick = onDeletePostClick,
            onReportClick = onReportClick,
            onRemoveClick = onRemoveClick,
            onLockPostClick = onLockPostClick,
            onViewSourceClick = onViewSourceClick,
            isCreator = account.id == postView.creator.id,
            canMod = canMod,
            viewSource = viewSource,
            showViewSource = fromPostActivity,
            scope = scope,
        )
    }

    val postActionbar = postActionbarMode.toEnum<PostActionbarMode>()

    val horizontalArrangement =
        when (postActionbar) {
            PostActionbarMode.Long -> Arrangement.spacedBy(XXL_PADDING)
            PostActionbarMode.LeftHandShort -> Arrangement.spacedBy(LARGE_PADDING)
            PostActionbarMode.RightHandShort -> Arrangement.spacedBy(LARGE_PADDING)
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
        if (postActionbar == PostActionbarMode.RightHandShort) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
                modifier = Modifier.weight(1F, true),
            )
        }

        VoteGeneric(
            myVote = instantScores.myVote,
            votes = instantScores.upvotes,
            type = VoteType.Upvote,
            showNumber = (instantScores.downvotes != 0) && showScores,
            onVoteClick = onUpvoteClick,
            account = account,
        )
        if (enableDownVotes) {
            VoteGeneric(
                myVote = instantScores.myVote,
                votes = instantScores.downvotes,
                showNumber = showScores,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                account = account,
            )
        }

        if (postActionbar == PostActionbarMode.Long) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
                modifier = Modifier.weight(1F, true),
            )
        }

        if (showReply) {
            ActionBarButton(
                icon = Icons.Outlined.Comment,
                contentDescription = stringResource(R.string.postListing_reply),
                onClick = { onReplyClick(postView) },
                account = account,
            )
        }
        ActionBarButton(
            icon =
                if (postView.saved) {
                    Icons.Filled.Bookmark
                } else {
                    Icons.Outlined.BookmarkBorder
                },
            contentDescription =
                if (postView.saved) {
                    stringResource(R.string.removeBookmark)
                } else {
                    stringResource(R.string.addBookmark)
                },
            onClick = { onSaveClick(postView) },
            contentColor =
                if (postView.saved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
            account = account,
        )
        ActionBarButton(
            icon = Icons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.moreOptions),
            account = account,
            onClick = { showMoreOptions = !showMoreOptions },
            requiresAccount = false,
            modifier = if (postActionbar == PostActionbarMode.LeftHandShort) Modifier.weight(1F, true) else Modifier,
        )

        if (postActionbar == PostActionbarMode.LeftHandShort) {
            CommentNewCountRework(
                comments = postView.counts.comments,
                unreadCount = postView.unread_comments,
                account = account,
            )
        }
    }
}

@Composable
fun CommentNewCountRework(
    comments: Int,
    unreadCount: Int,
    account: Account,
    modifier: Modifier = Modifier,
) {
    val unread =
        if (unreadCount == 0 || comments == unreadCount) {
            null
        } else {
            (if (unreadCount > 0) "+" else "") + siFormat(unreadCount)
        }

    ActionBarButtonAndBadge(
        icon = Icons.Outlined.Forum,
        iconBadgeCount = unread,
        contentDescription = null,
        text = siFormat(comments),
        noClick = true,
        account = account,
        onClick = {},
        modifier = modifier,
    )
}

@Composable
fun CommentNewCount(
    comments: Int,
    unreadCount: Int,
    style: TextStyle = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
    spacing: Dp = 0.dp,
) {
    val unread =
        if (unreadCount == 0 || comments == unreadCount) {
            null
        } else {
            unreadCount
        }
    if (unread != null) {
        Spacer(Modifier.padding(horizontal = spacing))

        Text(
            text = stringResource(R.string.post_listing_new, unread),
            style = style,
            color = MaterialTheme.colorScheme.onSurface.muted,
        )
    }
}

@Preview
@Composable
fun CommentCountPreview() {
    CommentNewCountRework(42, 0, account = AnonAccount)
}

@Preview
@Composable
fun PostFooterLinePreview() {
    val postView = samplePostView
    val instantScores =
        InstantScores(
            myVote = postView.my_vote,
            score = postView.counts.score,
            upvotes = postView.counts.upvotes,
            downvotes = postView.counts.downvotes,
        )
    PostFooterLine(
        postView = postView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
        instantScores = instantScores,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onViewSourceClick = {},
        account = AnonAccount,
        enableDownVotes = true,
        viewSource = false,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
        fromPostActivity = true,
        scope = rememberCoroutineScope(),
    )
}

@Preview
@Composable
fun PreviewPostListingCard() {
    PostListing(
        postView = samplePostView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
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
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListing(
        postView = sampleLinkPostView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
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
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Preview
@Composable
fun PreviewImagePostListingCard() {
    PostListing(
        postView = sampleImagePostView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
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
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Preview
@Composable
fun PreviewImagePostListingSmallCard() {
    PostListing(
        postView = sampleImagePostView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
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
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.SmallCard,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListing(
        postView = sampleLinkNoThumbnailPostView,
        admins = persistentListOf(),
        moderators = persistentListOf(),
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
        onReportClick = {},
        onRemoveClick = {},
        onLockPostClick = {},
        onPersonClick = {},
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showPostLinkPreview = true,
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Composable
fun PostListing(
    postView: PostView,
    admins: ImmutableList<PersonView>,
    moderators: ImmutableList<CommunityModeratorView>?,
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
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    showReply: Boolean = false,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: Int,
    appState: JerboaAppState,
    showPostLinkPreview: Boolean,
    showIfRead: Boolean,
    showScores: Boolean,
    postActionbarMode: Int,
) {
    // This stores vote data
    val instantScores =
        remember {
            mutableStateOf(
                InstantScores(
                    myVote = postView.my_vote,
                    score = postView.counts.score,
                    upvotes = postView.counts.upvotes,
                    downvotes = postView.counts.downvotes,
                ),
            )
        }

    var viewSource by remember { mutableStateOf(false) }

    when (postViewMode) {
        PostViewMode.Card ->
            PostListingCard(
                postView = postView,
                admins = admins,
                moderators = moderators,
                instantScores = instantScores.value,
                onUpvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Upvote,
                        )
                    onUpvoteClick(postView)
                },
                onDownvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Downvote,
                        )
                    onDownvoteClick(postView)
                },
                onReplyClick = onReplyClick,
                onPostClick = onPostClick,
                onSaveClick = onSaveClick,
                onCommunityClick = onCommunityClick,
                onEditPostClick = onEditPostClick,
                onDeletePostClick = onDeletePostClick,
                onReportClick = onReportClick,
                onRemoveClick = onRemoveClick,
                onLockPostClick = onLockPostClick,
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
                showScores = showScores,
                postActionbarMode = postActionbarMode,
            )

        PostViewMode.SmallCard ->
            PostListingCard(
                postView = postView,
                admins = admins,
                moderators = moderators,
                instantScores = instantScores.value,
                onUpvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Upvote,
                        )
                    onUpvoteClick(postView)
                },
                onDownvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Downvote,
                        )
                    onDownvoteClick(postView)
                },
                onReplyClick = onReplyClick,
                onPostClick = onPostClick,
                onSaveClick = onSaveClick,
                onCommunityClick = onCommunityClick,
                onEditPostClick = onEditPostClick,
                onDeletePostClick = onDeletePostClick,
                onReportClick = onReportClick,
                onRemoveClick = onRemoveClick,
                onLockPostClick = onLockPostClick,
                onPersonClick = onPersonClick,
                onViewSourceClick = {
                    viewSource = !viewSource
                },
                viewSource = viewSource,
                showReply = showReply,
                showCommunityName = showCommunityName,
                fullBody = false,
                account = account,
                expandedImage = false,
                enableDownVotes = enableDownVotes,
                showAvatar = showAvatar,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                showPostLinkPreview = showPostLinkPreview,
                appState = appState,
                showScores = showScores,
                postActionbarMode = postActionbarMode,
            )

        PostViewMode.List ->
            PostListingList(
                postView = postView,
                instantScores = instantScores.value,
                onUpvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Upvote,
                        )
                    onUpvoteClick(postView)
                },
                onDownvoteClick = {
                    instantScores.value =
                        calculateNewInstantScores(
                            instantScores.value,
                            voteType = VoteType.Downvote,
                        )
                    onDownvoteClick(postView)
                },
                onPostClick = onPostClick,
                showCommunityName = showCommunityName,
                account = account,
                showVotingArrowsInListView = showVotingArrowsInListView,
                showAvatar = showAvatar,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                appState = appState,
                showIfRead = showIfRead,
                enableDownVotes = enableDownVotes,
                showScores = showScores,
            )
    }
}

@Composable
fun PostVotingTile(
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    account: Account,
    enableDownVotes: Boolean,
    showScores: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(end = MEDIUM_PADDING),
    ) {
        VoteGeneric(
            myVote = instantScores.myVote,
            votes = instantScores.upvotes,
            type = VoteType.Upvote,
            showNumber = false,
            onVoteClick = onUpvoteClick,
            account = account,
        )
        Text(
            text = instantScores.score.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = scoreColor(myVote = instantScores.myVote),
            modifier = Modifier.alpha(if (showScores) 1f else 0f),
        )

        if (enableDownVotes) {
            // invisible Text below aligns width of PostVotingTiles
            Text(
                text = "00000",
                modifier = Modifier.height(0.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            VoteGeneric(
                myVote = instantScores.myVote,
                votes = instantScores.downvotes,
                type = VoteType.Downvote,
                showNumber = false,
                onVoteClick = onDownvoteClick,
                account = account,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostListingList(
    postView: PostView,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    showCommunityName: Boolean = true,
    account: Account,
    showVotingArrowsInListView: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    appState: JerboaAppState,
    showIfRead: Boolean,
    enableDownVotes: Boolean,
    showScores: Boolean,
) {
    Column(
        modifier =
            Modifier
                .padding(
                    horizontal = MEDIUM_PADDING,
                    vertical = MEDIUM_PADDING,
                )
                .testTag("jerboa:post"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(
                    SMALL_PADDING,
                ),
        ) {
            if (showVotingArrowsInListView) {
                PostVotingTile(
                    instantScores = instantScores,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    account = account,
                    enableDownVotes = enableDownVotes,
                    showScores = showScores,
                )
            }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable { onPostClick(postView) },
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            ) {
                PostName(postView = postView, showIfRead = showIfRead)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING, Alignment.Start),
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (showCommunityName) {
                        CommunityLink(
                            community = postView.community,
                            onClick = {},
                            clickable = false,
                            showDefaultIcon = false,
                            blurNSFW = blurNSFW,
                        )
                        DotSpacer(0.dp)
                    }
                    PersonProfileLink(
                        person = postView.creator,
                        isModerator = postView.creator_is_moderator,
                        isAdmin = postView.creator_is_admin,
                        onClick = {},
                        clickable = false,
                        color = MaterialTheme.colorScheme.onSurface.muted,
                        showAvatar = showAvatar,
                    )
                    DotSpacer(0.dp)
                    postView.post.url?.also { postUrl ->
                        if (!isSameInstance(postUrl, account.instance)) {
                            val hostName = hostName(postUrl)
                            hostName?.also {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onBackground.muted,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                DotSpacer(0.dp)
                            }
                        }
                    }
                    TimeAgo(published = postView.post.published, updated = postView.post.updated)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!showVotingArrowsInListView) {
                        Text(
                            text = instantScores.score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scoreColor(myVote = instantScores.myVote),
                        )
                        DotSpacer(0.dp)
                    }
                    Text(
                        text =
                            stringResource(
                                R.string.post_listing_comments_count,
                                postView.counts.comments,
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.muted,
                    )
                    CommentNewCount(
                        comments = postView.counts.comments,
                        unreadCount = postView.unread_comments,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    NsfwBadge(nsfwCheck(postView))
                }
            }
            ThumbnailTile(
                postView = postView,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                appState = appState,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ThumbnailTile(
    postView: PostView,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    appState: JerboaAppState,
) {
    postView.post.url?.also { url ->
        val postType = getPostType(url)

        val postLinkPicMod =
            Modifier
                .size(POST_LINK_PIC_SIZE)
                .combinedClickable(
                    onClick = {
                        if (postType != PostType.Link) {
                            appState.openImageViewer(url)
                        } else {
                            appState.openLink(
                                url,
                                useCustomTabs,
                                usePrivateTabs,
                            )
                        }
                    },
                    onLongClick = {
                        appState.showLinkPopup(url)
                    },
                )

        Box {
            postView.post.thumbnail_url?.also { thumbnail ->
                PictrsThumbnailImage(
                    thumbnail = thumbnail,
                    blur = blurNSFW.toEnum<BlurTypes>().needBlur(postView),
                    roundBottomEndCorner = postType != PostType.Link,
                    modifier = postLinkPicMod,
                )
            } ?: run {
                Card(
                    colors = CARD_COLORS,
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

            // Display a caret in the bottom right corner to denote this as an image
            if (postType != PostType.Link) {
                Icon(
                    painter = painterResource(id = R.drawable.triangle),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(THUMBNAIL_CARET_SIZE)
                            .align(Alignment.BottomEnd),
                    tint =
                        when (postType) {
                            PostType.Video -> MaterialTheme.jerboaColorScheme.videoHighlight
                            else -> MaterialTheme.jerboaColorScheme.imageHighlight
                        },
                )
            }
        }
    }
}

@Preview
@Composable
fun PostListingListPreview() {
    val postView = samplePostView
    val instantScores =
        InstantScores(
            myVote = postView.my_vote,
            score = postView.counts.score,
            upvotes = postView.counts.upvotes,
            downvotes = postView.counts.downvotes,
        )
    PostListingList(
        postView = postView,
        instantScores = instantScores,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        account = AnonAccount,
        showVotingArrowsInListView = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        enableDownVotes = false,
        showScores = true,
    )
}

@Preview
@Composable
fun PostListingListWithThumbPreview() {
    val postView = sampleImagePostView
    val instantScores =
        InstantScores(
            myVote = postView.my_vote,
            score = postView.counts.score,
            upvotes = postView.counts.upvotes,
            downvotes = postView.counts.downvotes,
        )
    PostListingList(
        postView = postView,
        instantScores = instantScores,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        account = AnonAccount,
        showVotingArrowsInListView = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = 1,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        enableDownVotes = false,
        showScores = true,
    )
}

@Composable
fun PostListingCard(
    postView: PostView,
    admins: ImmutableList<PersonView>,
    moderators: ImmutableList<CommunityModeratorView>?,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onViewSourceClick: () -> Unit,
    viewSource: Boolean,
    showReply: Boolean = false,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account,
    expandedImage: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    showPostLinkPreview: Boolean,
    appState: JerboaAppState,
    showIfRead: Boolean = false,
    showScores: Boolean,
    postActionbarMode: Int,
) {
    Column(
        modifier =
            Modifier
                .padding(vertical = MEDIUM_PADDING)
                .clickable { onPostClick(postView) }
                .testTag("jerboa:post"),
        // see https://stackoverflow.com/questions/77010371/prevent-popup-from-adding-padding-in-a-column-with-arrangement-spacedbylarge-p
        // verticalArrangement = Arrangement.spacedBy(LARGE_PADDING),
    ) {
        // Header
        PostHeaderLine(
            postView = postView,
            myVote = instantScores.myVote,
            score = instantScores.score,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            showCommunityName = showCommunityName,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            showAvatar = showAvatar,
            blurNSFW = blurNSFW,
            showScores = showScores,
        )

        Spacer(modifier = Modifier.padding(vertical = LARGE_PADDING))

        //  Title + metadata
        PostBody(
            postView = postView,
            fullBody = fullBody,
            viewSource = viewSource,
            expandedImage = expandedImage,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            showPostLinkPreview = showPostLinkPreview,
            appState = appState,
            clickBody = { onPostClick(postView) },
            showIfRead = showIfRead,
        )

        Spacer(modifier = Modifier.padding(vertical = LARGE_PADDING))

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
            onReportClick = onReportClick,
            onRemoveClick = onRemoveClick,
            onLockPostClick = onLockPostClick,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            onViewSourceClick = onViewSourceClick,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            showReply = showReply,
            account = account,
            enableDownVotes = enableDownVotes,
            viewSource = viewSource,
            showScores = showScores,
            postActionbarMode = postActionbarMode,
            fromPostActivity = fullBody,
            scope = appState.coroutineScope,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PostListingHeaderPreview() {
    SimpleTopAppBar("Post", onClickBack = {})
}

@Composable
fun MetadataCard(post: Post) {
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier =
            Modifier
                .padding(vertical = MEDIUM_PADDING, horizontal = MEDIUM_PADDING)
                .fillMaxWidth(),
        content = {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING),
            ) {
                Text(
                    text = post.embed_title!!,
                    style = MaterialTheme.typography.titleLarge,
                )
                post.embed_description?.also {
                    Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
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
