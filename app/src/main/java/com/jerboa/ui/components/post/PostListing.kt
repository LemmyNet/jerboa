package com.jerboa.ui.components.post

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Textsms
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.PostViewMode
import com.jerboa.VoteType
import com.jerboa.communityNameShown
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.Post
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.sampleImagePostView
import com.jerboa.datatypes.sampleLinkNoThumbnailPostView
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.Account
import com.jerboa.hostName
import com.jerboa.isImage
import com.jerboa.isSameInstance
import com.jerboa.nsfwCheck
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.PreviewLines
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
import com.jerboa.ui.theme.muted

@Composable
fun PostHeaderLine(
    postView: PostView,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
    modifier: Modifier = Modifier,
    showCommunityName: Boolean = true
) {
    val community = postView.community
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(LARGE_PADDING),
                modifier = Modifier.weight(1f)
            ) {
                if (showCommunityName) {
                    community.icon?.let {
                        CircularIcon(
                            icon = it,
                            size = MEDIUM_ICON_SIZE,
                            modifier = Modifier.clickable { onCommunityClick(community) },
                            thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)) {
                    if (showCommunityName) {
                        CommunityName(
                            community = postView.community,
                            modifier = Modifier.clickable { onCommunityClick(community) }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
                    ) {
                        PersonProfileLink(
                            person = postView.creator,
                            onClick = onPersonClick,
                            showTags = true,
                            isPostCreator = false, // Set this to false, we already know this
                            isModerator = isModerator,
                            isCommunityBanned = postView.creator_banned_from_community,
                            color = MaterialTheme.colorScheme.onSurface.muted
                        )
                        if (postView.post.featured_local) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = "TODO",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE)
                            )
                        }
                        if (postView.post.featured_community) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = "TODO",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE)
                            )
                        }
                        if (postView.post.locked) {
                            DotSpacer(0.dp)
                            Icon(
                                imageVector = Icons.Outlined.CommentsDisabled,
                                contentDescription = "TODO",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(ACTION_BAR_ICON_SIZE)
                            )
                        }
                    }
                }
            }
            ScoreAndTime(
                score = postView.counts.score,
                myVote = postView.my_vote,
                published = postView.post.published,
                updated = postView.post.updated
            )
        }
        Row {
            if (postView.post.deleted) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colorScheme.error
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
        isModerator = false,
        onCommunityClick = {},
        onPersonClick = {}
    )
}

@Composable
fun PostNodeHeader(
    postView: PostView,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean
) {
    CommentOrPostNodeHeader(
        creator = postView.creator,
        score = postView.counts.score,
        myVote = postView.my_vote,
        published = postView.post.published,
        updated = postView.post.updated,
        deleted = postView.post.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = true,
        isModerator = isModerator,
        isCommunityBanned = postView.creator_banned_from_community
    )
}

@Composable
fun PostTitleBlock(
    postView: PostView,
    expandedImage: Boolean,
    onPostLinkClick: (url: String) -> Unit,
    account: Account?
) {
    val imagePost = postView.post.url?.let { isImage(it) } ?: run { false }

    if (imagePost && expandedImage) {
        PostTitleAndImageLink(
            postView = postView,
            onPostLinkClick = onPostLinkClick
        )
    } else {
        PostTitleAndThumbnail(
            postView = postView,
            onPostLinkClick = onPostLinkClick,
            account = account
        )
    }
}

@Composable
fun PostName(
    postView: PostView
) {
    var color = if (postView.post.featured_local) {
        MaterialTheme.colorScheme.primary
    } else if (postView.post.featured_community) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    if (postView.read) {
        color = color.muted
    }

    Text(
        text = postView.post.name,
        style = MaterialTheme.typography.titleLarge,
        color = color
    )
}

@Composable
fun PostTitleAndImageLink(
    postView: PostView,
    onPostLinkClick: (url: String) -> Unit
) {
    // This was tested, we know it exists
    val url = postView.post.url!!

    Column(
        modifier = Modifier.padding(
            vertical = MEDIUM_PADDING,
            horizontal = LARGE_PADDING
        )

    ) {
        // Title of the post
        PostName(
            postView = postView
        )
    }

    val postLinkPicMod = Modifier
        .clickable { onPostLinkClick(url) }
    PictrsUrlImage(
        url = url,
        nsfw = nsfwCheck(postView),
        modifier = postLinkPicMod
    )
}

@Composable
fun PostTitleAndThumbnail(
    postView: PostView,
    onPostLinkClick: (url: String) -> Unit,
    account: Account?
) {
    val post = postView.post
    Column(
        modifier = Modifier.padding(horizontal = LARGE_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
        ) {
            // Title of the post
            Column(
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
                modifier = Modifier.weight(1f)
            ) {
                PostName(postView = postView)
                postView.post.url?.also { postUrl ->
                    if (!isSameInstance(postUrl, account?.instance)) {
                        val hostName = hostName(postUrl)
                        hostName?.also {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onBackground.muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            ThumbnailTile(postView = postView, onPostLinkClick = onPostLinkClick)
        }
    }
}

@Composable
fun PostBody(
    postView: PostView,
    fullBody: Boolean,
    expandedImage: Boolean,
    onPostLinkClick: (rl: String) -> Unit,
    account: Account?
) {
    val post = postView.post
    Column(
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        PostTitleBlock(
            postView = postView,
            expandedImage = expandedImage,
            onPostLinkClick = onPostLinkClick,
            account = account
        )

        // The metadata card
        if (fullBody && post.embed_title !== null) {
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
                    .padding(vertical = MEDIUM_PADDING, horizontal = LARGE_PADDING)
                    .fillMaxWidth(),
                content = {
                    if (fullBody) {
                        Column(
                            modifier = Modifier
                                .padding(MEDIUM_PADDING)
                        ) {
                            MyMarkdownText(
                                markdown = text
                            )
                        }
                    } else {
                        PreviewLines(
                            text = text,
                            modifier = Modifier
                                .padding(MEDIUM_PADDING)
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewStoryTitleAndMetadata() {
    PostBody(
        postView = samplePostView,
        onPostLinkClick = {},
        fullBody = false,
        expandedImage = false,
        account = null
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (person: PersonSafe) -> Unit,
    onBlockCommunityClick: (community: CommunitySafe) -> Unit,
    modifier: Modifier = Modifier,
    showReply: Boolean = false,
    account: Account?
) {
    // TODO val score = postView.counts.score
    val myVote = postView.my_vote
    val upvotes = postView.counts.upvotes
    val downvotes = postView.counts.downvotes

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
            isCreator = account?.id == postView.creator.id
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = SMALL_PADDING)
    ) {
        CommentCount(
            comments = postView.counts.comments,
            unreadCount = postView.unread_comments,
            account = account
        )
        VoteGeneric(
            myVote = myVote,
            votes = upvotes,
            item = postView,
            type = VoteType.Upvote,
            showNumber = (downvotes != 0),
            onVoteClick = {
                onUpvoteClick(it)
            },
            account = account
        )
        VoteGeneric(
            myVote = myVote,
            votes = downvotes,
            item = postView,
            type = VoteType.Downvote,
            onVoteClick = {
                onDownvoteClick(it)
            },
            account = account
        )
        ActionBarButton(
            icon = if (postView.saved) { Icons.Filled.Bookmark } else {
                Icons.Outlined.BookmarkBorder
            },
            onClick = { onSaveClick(postView) },
            contentColor = if (postView.saved) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground.muted
            },
            account = account
        )
        if (showReply) {
            ActionBarButton(
                icon = Icons.Outlined.Textsms,
                onClick = { onReplyClick(postView) },
                account = account
            )
        }
        ActionBarButton(
            icon = Icons.Outlined.MoreVert,
            account = account,
            onClick = { showMoreOptions = !showMoreOptions }
        )
    }
}

@Composable
fun CommentCount(
    comments: Int,
    unreadCount: Int,
    account: Account?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionBarButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            text = comments.toString(),
            noClick = true,
            account = account,
            onClick = {} // This is handled by the whole button click
        )
        CommentNewCount(
            comments = comments,
            unreadCount = unreadCount,
            spacing = SMALL_PADDING
        )
    }
}

@Composable
fun CommentNewCount(
    comments: Int,
    unreadCount: Int,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    spacing: Dp = 0.dp
) {
    val unread = if (unreadCount == 0 || comments == unreadCount) {
        null
    } else {
        unreadCount
    }
    if (unread != null) {
        Spacer(Modifier.padding(horizontal = spacing))

        Text(
            text = "( $unread new )",
            style = style,
            color = MaterialTheme.colorScheme.tertiary.muted
        )
    }
}

@Preview
@Composable
fun CommentCountPreview() {
    CommentCount(42, 0, account = null)
}

@Preview
@Composable
fun PostFooterLinePreview() {
    PostFooterLine(
        postView = samplePostView,
        account = null,
        onReportClick = {},
        onCommunityClick = {},
        onUpvoteClick = {},
        onSaveClick = {},
        onReplyClick = {},
        onDownvoteClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCreatorClick = {},
        onBlockCommunityClick = {}
    )
}

@Preview
@Composable
fun PreviewPostListingCard() {
    PostListing(
        postView = samplePostView,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        isModerator = true,
        fullBody = false,
        account = null,
        postViewMode = PostViewMode.Card
    )
}

@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListing(
        postView = sampleLinkPostView,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        isModerator = false,
        fullBody = false,
        account = null,
        postViewMode = PostViewMode.Card
    )
}

@Preview
@Composable
fun PreviewImagePostListingCard() {
    PostListing(
        postView = sampleImagePostView,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        isModerator = false,
        fullBody = false,
        account = null,
        postViewMode = PostViewMode.Card
    )
}

@Preview
@Composable
fun PreviewImagePostListingSmallCard() {
    PostListing(
        postView = sampleImagePostView,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        isModerator = false,
        fullBody = false,
        account = null,
        postViewMode = PostViewMode.SmallCard
    )
}

@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListing(
        postView = sampleLinkNoThumbnailPostView,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        isModerator = true,
        fullBody = false,
        account = null,
        postViewMode = PostViewMode.Card
    )
}

@Composable
fun PostListing(
    postView: PostView,
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit,
    onPostLinkClick: (url: String) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (person: PersonSafe) -> Unit,
    showReply: Boolean = false,
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account?,
    postViewMode: PostViewMode
) {
    when (postViewMode) {
        PostViewMode.Card -> PostListingCard(
            postView = postView,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onReplyClick = onReplyClick,
            onPostClick = onPostClick,
            onPostLinkClick = onPostLinkClick,
            onSaveClick = onSaveClick,
            onCommunityClick = onCommunityClick,
            onEditPostClick = onEditPostClick,
            onDeletePostClick = onDeletePostClick,
            onReportClick = onReportClick,
            onPersonClick = onPersonClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            showReply = showReply,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            fullBody = fullBody,
            account = account,
            expandedImage = true
        )
        PostViewMode.SmallCard -> PostListingCard(
            postView = postView,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onReplyClick = onReplyClick,
            onPostClick = onPostClick,
            onPostLinkClick = onPostLinkClick,
            onSaveClick = onSaveClick,
            onCommunityClick = onCommunityClick,
            onEditPostClick = onEditPostClick,
            onDeletePostClick = onDeletePostClick,
            onReportClick = onReportClick,
            onPersonClick = onPersonClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            showReply = showReply,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            account = account,
            fullBody = false,
            expandedImage = false
        )
        PostViewMode.List -> PostListingList(
            postView = postView,
            onPostClick = onPostClick,
            onPostLinkClick = onPostLinkClick,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            account = account
        )
    }
}

@Composable
fun PostListingList(
    postView: PostView,
    onPostClick: (postView: PostView) -> Unit,
    onPostLinkClick: (url: String) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    account: Account?
) {
    Column(
        modifier = Modifier.padding(
            horizontal = LARGE_PADDING,
            vertical = MEDIUM_PADDING
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                SMALL_PADDING
            )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPostClick(postView) },

                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
            ) {
                PostName(postView = postView)
                FlowRow(
                    mainAxisAlignment = FlowMainAxisAlignment.Start,
                    mainAxisSpacing = SMALL_PADDING,
                    crossAxisAlignment = FlowCrossAxisAlignment.Center
                ) {
                    if (showCommunityName) {
                        CommunityLink(
                            community = postView.community,
                            onClick = onCommunityClick
                        )
                        DotSpacer(0.dp)
                    }
                    PersonProfileLink(
                        person = postView.creator,
                        isModerator = isModerator,
                        onClick = onPersonClick,
                        color = MaterialTheme.colorScheme.onSurface.muted
                    )
                    DotSpacer(0.dp)
                    postView.post.url?.also { postUrl ->
                        if (!isSameInstance(postUrl, account?.instance)) {
                            val hostName = hostName(postUrl)
                            hostName?.also {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onBackground.muted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                DotSpacer(0.dp)
                            }
                        }
                    }
                    TimeAgo(published = postView.post.published, updated = postView.post.updated)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = postView.counts.score.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = scoreColor(myVote = postView.my_vote)
                    )
                    DotSpacer(0.dp)
                    Text(
                        text = "${postView.counts.comments} comments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.muted
                    )
                    CommentNewCount(
                        comments = postView.counts.comments,
                        unreadCount = postView.unread_comments,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            ThumbnailTile(postView, onPostLinkClick)
        }
    }
}

@Composable
private fun ThumbnailTile(
    postView: PostView,
    onPostLinkClick: (url: String) -> Unit
) {
    postView.post.url?.also { url ->
        val postLinkPicMod = Modifier
            .size(POST_LINK_PIC_SIZE)
            .clickable { onPostLinkClick(url) }

        postView.post.thumbnail_url?.also { thumbnail ->
            PictrsThumbnailImage(
                thumbnail = thumbnail,
                nsfw = nsfwCheck(postView),
                modifier = postLinkPicMod
            )
        } ?: run {
            Card(
                colors = CARD_COLORS,
                modifier = postLinkPicMod,
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = "TODO",
                        modifier = Modifier.size(LINK_ICON_SIZE)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PostListingListPreview() {
    PostListingList(
        postView = samplePostView,
        onPostClick = {},
        onPostLinkClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        isModerator = false,
        account = null
    )
}

@Preview
@Composable
fun PostListingListWithThumbPreview() {
    PostListingList(
        postView = sampleImagePostView,
        onPostClick = {},
        onPostLinkClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        isModerator = false,
        account = null
    )
}

@Composable
fun PostListingCard(
    postView: PostView,
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit,
    onPostLinkClick: (url: String) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (person: PersonSafe) -> Unit,
    showReply: Boolean = false,
    isModerator: Boolean,
    showCommunityName: Boolean = true,
    fullBody: Boolean,
    account: Account?,
    expandedImage: Boolean
) {
    Column(
        modifier = Modifier
            .padding(vertical = MEDIUM_PADDING)
            .clickable { onPostClick(postView) },
        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)
    ) {
        // Header
        PostHeaderLine(
            postView = postView,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            isModerator = isModerator,
            showCommunityName = showCommunityName,
            modifier = Modifier.padding(horizontal = LARGE_PADDING)
        )

        //  Title + metadata
        PostBody(
            postView = postView,
            onPostLinkClick = onPostLinkClick,
            fullBody = fullBody,
            expandedImage = expandedImage,
            account = account
        )

        // Footer bar
        PostFooterLine(
            postView = postView,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onSaveClick = onSaveClick,
            onReplyClick = onReplyClick,
            onCommunityClick = onCommunityClick,
            onEditPostClick = onEditPostClick,
            onDeletePostClick = onDeletePostClick,
            onReportClick = onReportClick,
            onBlockCommunityClick = onBlockCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            showReply = showReply,
            account = account,
            modifier = Modifier.padding(horizontal = LARGE_PADDING)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PostListingHeaderPreview() {
    val navController = rememberNavController()
    SimpleTopAppBar("Post", navController)
}

@Composable
fun MetadataCard(post: Post) {
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(vertical = MEDIUM_PADDING, horizontal = LARGE_PADDING)
            .fillMaxWidth(),
        content = {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING)
            ) {
                Text(
                    text = post.embed_title!!,
                    style = MaterialTheme.typography.titleLarge
                )
                post.embed_description?.also {
                    Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
                    // This is actually html, but markdown can render it
                    MyMarkdownText(markdown = it)
                }
            }
        }
    )
}

@Composable
fun PostOptionsDialog(
    postView: PostView,
    onDismissRequest: () -> Unit,
    onCommunityClick: () -> Unit,
    onEditPostClick: () -> Unit,
    onDeletePostClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    onBlockCommunityClick: () -> Unit,
    isCreator: Boolean
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Go to ${communityNameShown(postView.community)}",
                    icon = Icons.Outlined.Forum,
                    onClick = {
                        onCommunityClick()
                    }
                )
                postView.post.url?.also {
                    IconAndTextDrawerItem(
                        text = "Copy link",
                        icon = Icons.Outlined.Link,
                        onClick = {
                            localClipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(ctx, "Link Copied", Toast.LENGTH_SHORT).show()
                            onDismissRequest()
                        }
                    )
                }
                IconAndTextDrawerItem(
                    text = "Copy Permalink",
                    icon = Icons.Outlined.Link,
                    onClick = {
                        val permalink = postView.post.ap_id
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(ctx, "Permalink Copied", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
                if (!isCreator) {
                    IconAndTextDrawerItem(
                        text = "Report Post",
                        icon = Icons.Outlined.Flag,
                        onClick = onReportClick
                    )
                    IconAndTextDrawerItem(
                        text = "Block ${postView.creator.name}",
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCreatorClick
                    )
                    IconAndTextDrawerItem(
                        text = "Block ${postView.community.name}",
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCommunityClick
                    )
                }
                if (isCreator) {
                    IconAndTextDrawerItem(
                        text = "Edit",
                        icon = Icons.Outlined.Edit,
                        onClick = onEditPostClick
                    )
                    val deleted = postView.post.deleted
                    if (deleted) {
                        IconAndTextDrawerItem(
                            text = "Restore",
                            icon = Icons.Outlined.Restore,
                            onClick = onDeletePostClick
                        )
                    } else {
                        IconAndTextDrawerItem(
                            text = "Delete",
                            icon = Icons.Outlined.Delete,
                            onClick = onDeletePostClick
                        )
                    }
                }
            }
        },
        confirmButton = {}
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
        onDismissRequest = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {}
    )
}
