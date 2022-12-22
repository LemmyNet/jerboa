@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.post

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkAdded
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
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
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.PreviewLines
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.LINK_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.POST_LINK_PIC_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun PostHeaderLine(
    postView: PostView,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
    modifier: Modifier = Modifier,
    isSameInstance: Boolean = false,
    showCommunityName: Boolean = true
) {
    FlowRow(
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
        modifier = modifier
    ) {
        if (postView.post.stickied) {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = "TODO",
                tint = MaterialTheme.colorScheme.onBackground.muted
            )
            DotSpacer(style = MaterialTheme.typography.bodyMedium)
        }
        if (postView.post.locked) {
            Icon(
                imageVector = Icons.Outlined.CommentsDisabled,
                contentDescription = "TODO",
                tint = MaterialTheme.colorScheme.error
            )
            DotSpacer(style = MaterialTheme.typography.bodyMedium)
        }
        if (postView.post.deleted) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "TODO",
                tint = MaterialTheme.colorScheme.error
            )
            DotSpacer(style = MaterialTheme.typography.bodyMedium)
        }
        if (showCommunityName) {
            CommunityLink(
                community = postView.community,
                onClick = onCommunityClick
            )
            DotSpacer(style = MaterialTheme.typography.bodyMedium)
        }
        PersonProfileLink(
            person = postView.creator,
            onClick = onPersonClick,
            showTags = true,
            isPostCreator = false, // Set this to false, we already know this
            isModerator = isModerator,
            isCommunityBanned = postView.creator_banned_from_community

        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        if (!isSameInstance) {
            postView.post.url?.also {
                Text(
                    text = hostName(it),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                    style = MaterialTheme.typography.bodyMedium
                )
                DotSpacer(style = MaterialTheme.typography.bodyMedium)
            }
        }

        TimeAgo(
            published = postView.post.published,
            updated = postView.post.updated
        )
    }
}

@Preview
@Composable
fun PostHeaderLinePreview() {
    val postView = samplePostView
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
    onPostLinkClick: (url: String) -> Unit
) {
    val imagePost = postView.post.url?.let { isImage(it) } ?: run { false }

    if (imagePost) {
        PostTitleAndImageLink(
            postView = postView,
            onPostLinkClick = onPostLinkClick
        )
    } else {
        PostTitleAndThumbnail(
            postView = postView,
            onPostLinkClick = onPostLinkClick
        )
    }
}

@Composable
fun PostTitleAndImageLink(
    postView: PostView,
    onPostLinkClick: (url: String) -> Unit
) {
    // This was tested, we know it exists
    val url = postView.post.url!!

    Column {
        // Title of the post
        Text(
            text = postView.post.name,
            style = MaterialTheme.typography.titleLarge,
            color = if (postView.read) { MaterialTheme.colorScheme.onBackground.muted } else { MaterialTheme.colorScheme.onSurface },
            modifier = Modifier.padding(bottom = MEDIUM_PADDING, start = LARGE_PADDING, end = LARGE_PADDING)
        )

        val postLinkPicMod = Modifier
            .clickable { onPostLinkClick(url) }
        PictrsUrlImage(
            url = url,
            modifier = postLinkPicMod
        )
    }
}

@Composable
fun PostTitleAndThumbnail(
    postView: PostView,
    onPostLinkClick: (url: String) -> Unit
) {
    val post = postView.post

    Row {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.titleLarge,
            color = if (postView.read) { MaterialTheme.colorScheme.onBackground.muted } else { MaterialTheme.colorScheme.onSurface },
            modifier = Modifier.weight(1f).padding(horizontal = LARGE_PADDING)
        )

        post.url?.also { url ->
            val postLinkPicMod = Modifier
                .size(POST_LINK_PIC_SIZE)
                .padding(
                    start = MEDIUM_PADDING,
                    end = MEDIUM_PADDING,
                    top = 0.dp,
                    bottom = XL_PADDING
                )
                .clickable { onPostLinkClick(url) }

            post.thumbnail_url?.also { thumbnail ->
                PictrsThumbnailImage(
                    thumbnail = thumbnail,
                    modifier = postLinkPicMod
                )
            } ?: run {
                Card(
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
}

@Composable
fun PostBody(
    postView: PostView,
    fullBody: Boolean,
    onPostLinkClick: (url: String) -> Unit
) {
    val post = postView.post
    Column(
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        PostTitleBlock(postView = postView, onPostLinkClick = onPostLinkClick)

        // The metadata card
        if (fullBody && post.embed_title !== null) {
            MetadataCard(post = post)
        }

        // Check to make sure body isn't empty string
        val body = post.body?.trim()?.ifEmpty { null }

        // The desc
        body?.also { text ->
            Card(
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
        fullBody = false
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
            account = account
        )
        VoteGeneric(
            myVote = myVote,
            votes = upvotes,
            item = postView,
            type = VoteType.Upvote,
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
            icon = if (postView.saved) { Icons.Outlined.BookmarkAdded } else {
                Icons.Outlined.BookmarkAdd
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
    account: Account?
) {
    ActionBarButton(
        icon = Icons.Outlined.ChatBubbleOutline,
        text = "$comments",
        noClick = true,
        account = account,
        onClick = {}
    )
}

@Preview
@Composable
fun CommentCountPreview() {
    CommentCount(42, account = null)
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
fun PreviewPostListing() {
    PostListing(
        postView = samplePostView,
        account = null,
        isModerator = true,
        onReportClick = {},
        onCommunityClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onSaveClick = {},
        onUpvoteClick = {},
        onPostLinkClick = {},
        onPersonClick = {},
        onPostClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        fullBody = false
    )
}

@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListing(
        postView = sampleLinkPostView,
        account = null,
        isModerator = false,
        onReportClick = {},
        onPersonClick = {},
        onCommunityClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onUpvoteClick = {},
        onDownvoteClick = {},
        onSaveClick = {},
        onReplyClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        fullBody = false
    )
}

@Preview
@Composable
fun PreviewImagePostListing() {
    PostListing(
        postView = sampleImagePostView,
        account = null,
        isModerator = false,
        onReportClick = {},
        onPersonClick = {},
        onCommunityClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onUpvoteClick = {},
        onDownvoteClick = {},
        onSaveClick = {},
        onReplyClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        fullBody = false
    )
}

@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListing(
        postView = sampleLinkNoThumbnailPostView,
        account = null,
        isModerator = true,
        onReportClick = {},
        onPersonClick = {},
        onCommunityClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onUpvoteClick = {},
        onDownvoteClick = {},
        onSaveClick = {},
        onReplyClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        fullBody = false
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
    account: Account?
) {
    Column(
        modifier = Modifier.padding(vertical = MEDIUM_PADDING)
            .clickable { onPostClick(postView) },
        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)
    ) {
        // Header
        PostHeaderLine(
            postView = postView,
            onCommunityClick = onCommunityClick,
            onPersonClick = onPersonClick,
            isModerator = isModerator,
            isSameInstance = postView.post.url?.let { hostName(it) } == account?.instance,
            showCommunityName = showCommunityName,
            modifier = Modifier.padding(horizontal = LARGE_PADDING)
        )

        //  Title + metadata
        PostBody(
            postView = postView,
            onPostLinkClick = onPostLinkClick,
            fullBody = fullBody
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
