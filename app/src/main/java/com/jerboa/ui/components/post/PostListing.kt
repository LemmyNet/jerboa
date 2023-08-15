package com.jerboa.ui.components.post

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerboa.InstantScores
import com.jerboa.PostType
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.calculateNewInstantScores
import com.jerboa.communityNameShown
import com.jerboa.copyToClipboard
import com.jerboa.datatypes.sampleImagePostView
import com.jerboa.datatypes.sampleLinkNoThumbnailPostView
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.sampleMarkdownPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.Person
import com.jerboa.datatypes.types.Post
import com.jerboa.datatypes.types.PostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.PostActionbarMode
import com.jerboa.getPostType
import com.jerboa.hostName
import com.jerboa.isSameInstance
import com.jerboa.nsfwCheck
import com.jerboa.siFormat
import com.jerboa.toEnum
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.ActionBarButtonAndBadge
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.IconAndTextDrawerItem
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

@Composable
fun PostHeaderLine(
    postView: PostView,
    myVote: Int?,
    score: Int,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
    modifier: Modifier = Modifier,
    showCommunityName: Boolean = true,
    showAvatar: Boolean,
    blurNSFW: Boolean,
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
                            blur = blurNSFW && community.nsfw,
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
                            isModerator = isModerator,
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
        Row {
            if (postView.post.deleted) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.postListing_deleted),
                    tint = MaterialTheme.colorScheme.error,
                )
                DotSpacer(0.dp)
            }
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
        isModerator = false,
        onCommunityClick = {},
        onPersonClick = {},
        showAvatar = true,
        blurNSFW = true,
        showScores = true,
    )
}

@Composable
fun PostNodeHeader(
    postView: PostView,
    myVote: Int?,
    score: Int,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
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
        isModerator = isModerator,
        isCommunityBanned = postView.creator_banned_from_community,
        onClick = {},
        onLongCLick = {},
        showAvatar = showAvatar,
        showScores = showScores,
    )
}

@Composable
fun PostTitleBlock(
    postView: PostView,
    expandedImage: Boolean,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
    showIfRead: Boolean,
) {
    val imagePost = postView.post.url?.let { getPostType(it) == PostType.Image } ?: false

    if (imagePost && expandedImage) {
        PostTitleAndImageLink(
            postView = postView,
            blurNSFW = blurNSFW,
            openImageViewer = openImageViewer,
            showIfRead = showIfRead,
        )
    } else {
        PostTitleAndThumbnail(
            postView = postView,
            account = account,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            openLink = openLink,
            openImageViewer = openImageViewer,
            showIfRead = showIfRead,
        )
    }
}

@Composable
fun PostName(
    postView: PostView,
    showIfRead: Boolean,
) {
    var color = if (postView.post.featured_local) {
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

@Composable
fun PostTitleAndImageLink(
    postView: PostView,
    blurNSFW: Boolean,
    openImageViewer: (url: String) -> Unit,
    showIfRead: Boolean,
) {
    // This was tested, we know it exists
    val url = postView.post.url!!

    Column(
        modifier = Modifier.padding(
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

    val postLinkPicMod = Modifier
        .clickable { openImageViewer(url) }
    PictrsUrlImage(
        url = url,
        blur = blurNSFW && nsfwCheck(postView),
        modifier = postLinkPicMod,
    )
}

@Composable
fun PostTitleAndThumbnail(
    postView: PostView,
    account: Account,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
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
                openLink = openLink,
                openImageViewer = openImageViewer,
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
    blurNSFW: Boolean,
    showPostLinkPreview: Boolean,
    openImageViewer: (url: String) -> Unit,
    openLink: (String, Boolean, Boolean) -> Unit,
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
            openImageViewer = openImageViewer,
            openLink = openLink,
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
                modifier = Modifier
                    .padding(vertical = MEDIUM_PADDING, horizontal = MEDIUM_PADDING)
                    .fillMaxWidth(),
                content = {
                    if (fullBody) {
                        Column(
                            modifier = Modifier
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
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
        showIfRead = true,
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCreatorClick: (person: Person) -> Unit,
    onBlockCommunityClick: (community: Community) -> Unit,
    onShareClick: (url: String) -> Unit,
    onViewSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
    showReply: Boolean = false,
    account: Account,
    enableDownVotes: Boolean,
    viewSource: Boolean,
    showScores: Boolean,
    postActionbarMode: Int,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        PostOptionsDialog(
            postView = postView,
            onDismissRequest = { showMoreOptions = false },
            onEditPostClick = {
                showMoreOptions = false
                onEditPostClick(postView)
            },
            onDeletePostClick = {
                showMoreOptions = false
                onDeletePostClick(postView)
            },
            onCommunityClick = {
                showMoreOptions = false
                onCommunityClick(postView.community)
            },
            onPersonClick = {
                showMoreOptions = false
                onPersonClick(postView.creator.id)
            },
            onReportClick = {
                showMoreOptions = false
                onReportClick(postView)
            },
            onBlockCommunityClick = {
                showMoreOptions = false
                onBlockCommunityClick(postView.community)
            },
            onBlockCreatorClick = {
                showMoreOptions = false
                onBlockCreatorClick(postView.creator)
            },
            onShareClick = { url ->
                showMoreOptions = false
                onShareClick(url)
            },
            onViewSourceClick = {
                showMoreOptions = false
                onViewSourceClick()
            },
            isCreator = account.id == postView.creator.id,
            viewSource = viewSource,
        )
    }

    val postActionbar = postActionbarMode.toEnum<PostActionbarMode>()

    val horizontalArrangement = when (postActionbar) {
        PostActionbarMode.Long -> Arrangement.spacedBy(XXL_PADDING)
        PostActionbarMode.LeftHandShort -> Arrangement.spacedBy(LARGE_PADDING)
        PostActionbarMode.RightHandShort -> Arrangement.spacedBy(LARGE_PADDING)
    }

    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
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
            icon = if (postView.saved) {
                Icons.Filled.Bookmark
            } else {
                Icons.Outlined.BookmarkBorder
            },
            contentDescription = if (postView.saved) {
                stringResource(R.string.removeBookmark)
            } else {
                stringResource(R.string.addBookmark)
            },
            onClick = { onSaveClick(postView) },
            contentColor = if (postView.saved) {
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
    val unread = if (unreadCount == 0 || comments == unreadCount) {
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
    val unread = if (unreadCount == 0 || comments == unreadCount) {
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
        instantScores = instantScores,
        account = AnonAccount,
        onReportClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onUpvoteClick = {},
        onSaveClick = {},
        onReplyClick = {},
        onDownvoteClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCreatorClick = {},
        onBlockCommunityClick = {},
        onShareClick = {},
        onViewSourceClick = {},
        enableDownVotes = true,
        viewSource = false,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Preview
@Composable
fun PreviewPostListingCard() {
    PostListing(
        postView = samplePostView,
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
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onShareClick = {},
        isModerator = true,
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onShareClick = {},
        isModerator = false,
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onShareClick = {},
        isModerator = false,
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onShareClick = {},
        isModerator = false,
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.SmallCard,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onShareClick = {},
        isModerator = true,
        fullBody = false,
        account = AnonAccount,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        showPostLinkPreview = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
        showIfRead = true,
        showScores = true,
        postActionbarMode = PostActionbarMode.Long.ordinal,
    )
}

@Composable
fun PostListing(
    postView: PostView,
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
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (person: Person) -> Unit,
    onShareClick: (url: String) -> Unit,
    showReply: Boolean = false,
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
    showPostLinkPreview: Boolean,
    showIfRead: Boolean,
    showScores: Boolean,
    postActionbarMode: Int,
) {
    // This stores vote data
    val instantScores = remember {
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
        PostViewMode.Card -> PostListingCard(
            postView = postView,
            instantScores = instantScores.value,
            onUpvoteClick = {
                instantScores.value = calculateNewInstantScores(
                    instantScores.value,
                    voteType = VoteType.Upvote,
                )
                onUpvoteClick(postView)
            },
            onDownvoteClick = {
                instantScores.value = calculateNewInstantScores(
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
            onPersonClick = onPersonClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            onShareClick = onShareClick,
            onViewSourceClick = {
                viewSource = !viewSource
            },
            viewSource = viewSource,
            showReply = showReply,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            fullBody = fullBody,
            account = account,
            expandedImage = true,
            enableDownVotes = enableDownVotes,
            showAvatar = showAvatar,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            openLink = openLink,
            openImageViewer = openImageViewer,
            showPostLinkPreview = showPostLinkPreview,
            showIfRead = showIfRead,
            showScores = showScores,
            postActionbarMode = postActionbarMode,
        )

        PostViewMode.SmallCard -> PostListingCard(
            postView = postView,
            instantScores = instantScores.value,
            onUpvoteClick = {
                instantScores.value = calculateNewInstantScores(
                    instantScores.value,
                    voteType = VoteType.Upvote,
                )
                onUpvoteClick(postView)
            },
            onDownvoteClick = {
                instantScores.value = calculateNewInstantScores(
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
            onPersonClick = onPersonClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            onShareClick = onShareClick,
            onViewSourceClick = {
                viewSource = !viewSource
            },
            viewSource = viewSource,
            showReply = showReply,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            account = account,
            fullBody = false,
            expandedImage = false,
            enableDownVotes = enableDownVotes,
            showAvatar = showAvatar,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            openLink = openLink,
            showPostLinkPreview = showPostLinkPreview,
            openImageViewer = openImageViewer,
            showScores = showScores,
            postActionbarMode = postActionbarMode,
        )

        PostViewMode.List -> PostListingList(
            postView = postView,
            instantScores = instantScores.value,
            onUpvoteClick = {
                instantScores.value = calculateNewInstantScores(
                    instantScores.value,
                    voteType = VoteType.Upvote,
                )
                onUpvoteClick(postView)
            },
            onDownvoteClick = {
                instantScores.value = calculateNewInstantScores(
                    instantScores.value,
                    voteType = VoteType.Downvote,
                )
                onDownvoteClick(postView)
            },
            onPostClick = onPostClick,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            account = account,
            showVotingArrowsInListView = showVotingArrowsInListView,
            showAvatar = showAvatar,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            openImageViewer = openImageViewer,
            openLink = openLink,
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
        modifier = Modifier
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
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    account: Account,
    showVotingArrowsInListView: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
    showIfRead: Boolean,
    enableDownVotes: Boolean,
    showScores: Boolean,
) {
    Column(
        modifier = Modifier
            .padding(
                horizontal = MEDIUM_PADDING,
                vertical = MEDIUM_PADDING,
            )
            .testTag("jerboa:post"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
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
                modifier = Modifier
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
                        isModerator = isModerator,
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
                        text = stringResource(
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
                openLink = openLink,
                openImageViewer = openImageViewer,
            )
        }
    }
}

@Composable
private fun ThumbnailTile(
    postView: PostView,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
) {
    postView.post.url?.also { url ->
        val postType = getPostType(url)

        val postLinkPicMod = Modifier
            .size(POST_LINK_PIC_SIZE)
            .clickable {
                if (postType != PostType.Link) {
                    openImageViewer(url)
                } else {
                    openLink(
                        url,
                        useCustomTabs,
                        usePrivateTabs,
                    )
                }
            }

        Box {
            postView.post.thumbnail_url?.also { thumbnail ->
                PictrsThumbnailImage(
                    thumbnail = thumbnail,
                    blur = blurNSFW && nsfwCheck(postView),
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
                    modifier = Modifier
                        .size(THUMBNAIL_CARET_SIZE)
                        .align(Alignment.BottomEnd),
                    tint = when (postType) {
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
        isModerator = false,
        account = AnonAccount,
        showVotingArrowsInListView = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
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
        isModerator = false,
        account = AnonAccount,
        showVotingArrowsInListView = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = true,
        openImageViewer = {},
        openLink = { _: String, _: Boolean, _: Boolean -> },
        showIfRead = true,
        enableDownVotes = false,
        showScores = true,
    )
}

@Composable
fun PostListingCard(
    postView: PostView,
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
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (person: Person) -> Unit,
    onShareClick: (url: String) -> Unit,
    onViewSourceClick: () -> Unit,
    viewSource: Boolean,
    showReply: Boolean = false,
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account,
    expandedImage: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    showPostLinkPreview: Boolean,
    openLink: (String, Boolean, Boolean) -> Unit,
    openImageViewer: (url: String) -> Unit,
    showIfRead: Boolean = false,
    showScores: Boolean,
    postActionbarMode: Int,
) {
    Column(
        modifier = Modifier
            .padding(vertical = MEDIUM_PADDING)
            .clickable { onPostClick(postView) }
            .testTag("jerboa:post"),
        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING),
    ) {
        // Header
        PostHeaderLine(
            postView = postView,
            myVote = instantScores.myVote,
            score = instantScores.score,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            showAvatar = showAvatar,
            blurNSFW = blurNSFW,
            showScores = showScores,
        )

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
            openImageViewer = openImageViewer,
            clickBody = { onPostClick(postView) },
            openLink = openLink,
            showIfRead = showIfRead,
        )

        // Footer bar
        PostFooterLine(
            postView = postView,
            instantScores = instantScores,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onSaveClick = onSaveClick,
            onReplyClick = onReplyClick,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            onEditPostClick = onEditPostClick,
            onDeletePostClick = onDeletePostClick,
            onReportClick = onReportClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            onViewSourceClick = onViewSourceClick,
            onShareClick = onShareClick,
            showReply = showReply,
            account = account,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            enableDownVotes = enableDownVotes,
            viewSource = viewSource,
            showScores = showScores,
            postActionbarMode = postActionbarMode,
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
        modifier = Modifier
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

@Composable
fun PostOptionsDialog(
    postView: PostView,
    onDismissRequest: () -> Unit,
    onCommunityClick: () -> Unit,
    onPersonClick: () -> Unit,
    onEditPostClick: () -> Unit,
    onDeletePostClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    onBlockCommunityClick: () -> Unit,
    onShareClick: (shareUrl: String) -> Unit,
    onViewSourceClick: () -> Unit,
    isCreator: Boolean,
    viewSource: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(
                        R.string.post_listing_go_to,
                        communityNameShown(postView.community),
                    ),
                    icon = Icons.Outlined.Forum,
                    onClick = {
                        onCommunityClick()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(
                        R.string.post_listing_go_to,
                        postView.creator.name,
                    ),
                    icon = Icons.Outlined.Person,
                    onClick = {
                        onPersonClick()
                    },
                )
                postView.post.url?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_copy_link),
                        icon = Icons.Outlined.Link,
                        onClick = {
                            localClipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_link_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                            onDismissRequest()
                        },
                    )
                }
                IconAndTextDrawerItem(
                    text = stringResource(R.string.post_listing_copy_permalink),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        val permalink = postView.post.ap_id
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.post_listing_permalink_copied),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onDismissRequest()
                    },
                )
                postView.post.thumbnail_url?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_copy_thumbnail_link),
                        icon = Icons.Outlined.Link,
                        onClick = {
                            if (copyToClipboard(
                                    ctx,
                                    postView.post.thumbnail_url,
                                    "thumbnail link",
                                )
                            ) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.post_listing_thumbnail_link_copied),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.generic_error),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            onDismissRequest()
                        },
                    )
                }
                postView.post.embed_description?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_copy_title),
                        icon = Icons.Outlined.ContentCopy,
                        onClick = {
                            if (copyToClipboard(
                                    ctx,
                                    postView.post.embed_description,
                                    "post title",
                                )
                            ) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.post_listing_title_copied),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.generic_error),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            onDismissRequest()
                        },
                    )
                }
                postView.post.name.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_copy_name),
                        icon = Icons.Outlined.ContentCopy,
                        onClick = {
                            if (copyToClipboard(ctx, postView.post.name, "post name")) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.post_listing_name_copied),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.generic_error),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            onDismissRequest()
                        },
                    )
                }
                postView.post.body?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_copy_text),
                        icon = Icons.Outlined.ContentCopy,
                        onClick = {
                            if (copyToClipboard(ctx, postView.post.body, "post text")) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.post_listing_text_copied),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.generic_error),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            onDismissRequest()
                        },
                    )
                }
                postView.post.body?.also {
                    IconAndTextDrawerItem(
                        text = if (viewSource) {
                            stringResource(R.string.post_listing_view_original)
                        } else {
                            stringResource(
                                R.string.post_listing_view_source,
                            )
                        },
                        icon = Icons.Outlined.Description,
                        onClick = onViewSourceClick,
                    )
                }
                postView.post.url?.also { url ->
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_share_link),
                        icon = Icons.Outlined.Share,
                        onClick = { onShareClick(url) },
                    )
                }
                IconAndTextDrawerItem(
                    text = stringResource(R.string.post_listing_share_post),
                    icon = Icons.Outlined.Share,
                    onClick = { onShareClick(postView.post.ap_id) },
                )
                if (!isCreator) {
                    Divider(Modifier.padding(LARGE_PADDING))
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_block, postView.creator.name),
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCreatorClick,
                    )
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_block, postView.community.name),
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCommunityClick,
                    )
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_report_post),
                        icon = Icons.Outlined.Flag,
                        onClick = onReportClick,
                    )
                }
                if (isCreator) {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.post_listing_edit),
                        icon = Icons.Outlined.Edit,
                        onClick = onEditPostClick,
                    )
                    val deleted = postView.post.deleted
                    if (deleted) {
                        IconAndTextDrawerItem(
                            text = stringResource(R.string.post_listing_restore),
                            icon = Icons.Outlined.Restore,
                            onClick = onDeletePostClick,
                        )
                    } else {
                        IconAndTextDrawerItem(
                            text = stringResource(R.string.post_listing_delete),
                            icon = Icons.Outlined.Delete,
                            onClick = onDeletePostClick,
                        )
                    }
                }
            }
        },
        confirmButton = {},
    )
}

@Preview
@Composable
fun PostOptionsDialogPreview() {
    PostOptionsDialog(
        postView = samplePostView,
        isCreator = true,
        onReportClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onDismissRequest = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCommunityClick = {},
        onShareClick = {},
        onBlockCreatorClick = {},
        onViewSourceClick = {},
        viewSource = true,
    )
}
