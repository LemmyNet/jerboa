package com.jerboa.ui.components.post

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.*
import com.jerboa.datatypes.*
import com.jerboa.db.Account
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.home.IconAndTextDrawerItem
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.*

@Composable
fun PostHeaderLine(
    postView: PostView,
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
    isModerator: Boolean,
) {
    FlowRow(
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        CommunityLink(
            community = postView.community,
            onClick = onCommunityClick,
        )
        DotSpacer()
        PersonProfileLink(
            person = postView.creator,
            onClick = onPersonClick,
            showTags = true,
            isPostCreator = false, // Set this to false, we already know this
            isModerator = isModerator,
            isCommunityBanned = postView.creator_banned_from_community,

        )
        DotSpacer()
        postView.post.url?.also {
            // TODO hide also if its the same instance / domain
            Text(text = hostName(it), color = Muted)
            DotSpacer()
        }
        TimeAgo(dateStr = postView.post.published)
    }
}

@Preview
@Composable
fun PostHeaderLinePreview() {
    val postView = samplePostView
    PostHeaderLine(postView = postView, isModerator = false)
}

@Composable
fun PostNodeHeader(
    postView: PostView,
    onPersonClick: (personId: Int) -> Unit = {},
    isModerator: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = postView.creator,
        score = postView.counts.score,
        myVote = postView.my_vote,
        published = postView.post.published,
        onPersonClick = onPersonClick,
        isPostCreator = true,
        isModerator = isModerator,
        isCommunityBanned = postView.creator_banned_from_community,
    )
}

@Composable
fun PostTitleBlock(
    post: Post,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    val imagePost = post.url?.let { isImage(it) } ?: run { false }

    if (imagePost) {
        PostTitleAndImageLink(
            post = post,
            onPostLinkClick = onPostLinkClick
        )
    } else {
        PostTitleAndThumbnail(
            post = post,
            onPostLinkClick = onPostLinkClick
        )
    }
}

@Composable
fun PostTitleAndImageLink(
    post: Post,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    // This was tested, we know it exists
    val url = post.url!!

    Column {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = MEDIUM_PADDING)
        )

        val postLinkPicMod = Modifier
            .fillMaxWidth()
            .clickable { onPostLinkClick(url) }
        PictrsUrlImage(
            url = url,
            modifier = postLinkPicMod,
        )
    }
}

@Composable
fun PostTitleAndThumbnail(
    post: Post,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    Row {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.weight(1f)
        )

        post.url?.also { url ->
            val postLinkPicMod = Modifier
                .size(POST_LINK_PIC_SIZE)
                .padding(
                    start = MEDIUM_PADDING,
                    end = MEDIUM_PADDING,
                    top = 0.dp,
                    bottom = XL_PADDING,
                )
                .clickable { onPostLinkClick(url) }

            post.thumbnail_url?.also { thumbnail ->
                PictrsThumbnailImage(
                    thumbnail = thumbnail,
                    modifier = postLinkPicMod,
                )
            } ?: run {
                Card(
                    modifier = postLinkPicMod,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Link,
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
    post: Post,
    fullBody: Boolean = false,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        PostTitleBlock(post = post, onPostLinkClick = onPostLinkClick)

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
                    .padding(vertical = MEDIUM_PADDING)
                    .fillMaxWidth(),
                backgroundColor = colorShade(MaterialTheme.colors.surface, 2.5f),
                content = {
                    if (fullBody) {
                        MyMarkdownText(
                            markdown = text,
                            modifier = Modifier
                                .padding(MEDIUM_PADDING),
                        )
                    } else {
                        PreviewLines(
                            text = text,
                            modifier = Modifier
                                .padding(MEDIUM_PADDING),
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
        post = samplePost
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    onReplyClick: (postView: PostView) -> Unit = {},
    onSaveClick: (postView: PostView) -> Unit = {},
    onEditPostClick: (postView: PostView) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    showReply: Boolean = false,
    myVote: Int?,
    upvotes: Int,
    downvotes: Int,
    account: Account?,
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
            onCommunityClick = {
                showMoreOptions = false
                onCommunityClick(postView.community)
            },
            isCreator = account?.id == postView.creator.id,
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SMALL_PADDING)
    ) {
        Row {
            CommentCount(
                comments = postView.counts.comments,
                account = account
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING)

        ) {
            VoteGeneric(
                myVote = myVote,
                votes = upvotes, item = postView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                account = account,
            )
            VoteGeneric(
                myVote = myVote,
                votes = downvotes, item = postView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                account = account,
            )
            ActionBarButton(
                icon = Icons.Default.Star,
                onClick = { onSaveClick(postView) },
                contentColor = if (postView.saved) {
                    Color.Yellow
                } else {
                    Muted
                },
                account = account,
            )
            if (showReply) {
                ActionBarButton(
                    icon = Icons.Default.Reply,
                    onClick = { onReplyClick(postView) },
                    account = account,
                )
            }
            ActionBarButton(
                icon = Icons.Default.MoreVert,
                account = account,
                onClick = { showMoreOptions = !showMoreOptions }
            )
        }
    }
}

@Composable
fun CommentCount(
    comments: Int,
    account: Account?,
) {
    ActionBarButton(
        icon = Icons.Default.ChatBubble,
        text = "$comments comments",
        noClick = true,
        account = account,
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
        myVote = -1,
        upvotes = 2,
        downvotes = 23,
        account = null,
    )
}

@Preview
@Composable
fun PreviewPostListing() {
    PostListing(
        postView = samplePostView,
        fullBody = true,
        account = null,
        isModerator = true
    )
}

@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListing(
        postView = sampleLinkPostView,
        fullBody = true,
        account = null,
        isModerator = false,
    )
}

@Preview
@Composable
fun PreviewImagePostListing() {
    PostListing(
        postView = sampleImagePostView,
        fullBody = true,
        account = null,
        isModerator = false,
    )
}

@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListing(
        postView = sampleLinkNoThumbnailPostView,
        fullBody = true,
        account = null,
        isModerator = true,
    )
}

@Composable
fun PostListing(
    postView: PostView,
    fullBody: Boolean = false,
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    onReplyClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit = {},
    onPostLinkClick: (url: String) -> Unit = {},
    onSaveClick: (postView: PostView) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onEditPostClick: (postView: PostView) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
    showReply: Boolean = false,
    isModerator: Boolean,
    account: Account?,
) {

    // These are necessary for instant post voting
    val score = remember { mutableStateOf(postView.counts.score) }
    val myVote = remember { mutableStateOf(postView.my_vote) }
    val upvotes = remember { mutableStateOf(postView.counts.upvotes) }
    val downvotes = remember { mutableStateOf(postView.counts.downvotes) }

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(vertical = SMALL_PADDING)
            .clickable { onPostClick(postView) }
    ) {
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
        ) {

            // Header
            PostHeaderLine(
                postView = postView,
                onCommunityClick = onCommunityClick,
                onPersonClick = onPersonClick,
                isModerator = isModerator,
            )

            //  Title + metadata
            PostBody(
                post = postView.post,
                fullBody,
                onPostLinkClick = onPostLinkClick,
            )

            // Footer bar
            PostFooterLine(
                postView = postView,
                onUpvoteClick = {
                    handleInstantUpvote(myVote, score, upvotes, downvotes)
                    onUpvoteClick(it)
                },
                onDownvoteClick = {
                    handleInstantDownvote(myVote, score, upvotes, downvotes)
                    onDownvoteClick(it)
                },
                onSaveClick = onSaveClick,
                onReplyClick = onReplyClick,
                onCommunityClick = onCommunityClick,
                onEditPostClick = onEditPostClick,
                showReply = showReply,
                myVote = myVote.value,
                upvotes = upvotes.value,
                downvotes = downvotes.value,
                account = account,
            )
        }
    }
}

@Preview
@Composable
fun PostListingHeaderPreview() {
    val navController = rememberNavController()
    SimpleTopAppBar("Post", navController)
}

fun postClickWrapper(
    postViewModel: PostViewModel,
    postId: Int,
    account: Account?,
    navController: NavController,
    ctx: Context,
) {
    postViewModel.fetchPost(
        id = postId,
        account = account,
        clear = true,
        ctx = ctx,
    )
    navController.navigate(route = "post")
}

@Composable
fun MetadataCard(post: Post) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(vertical = MEDIUM_PADDING)
            .fillMaxWidth(),
        backgroundColor = colorShade(MaterialTheme.colors.surface, 2.5f),
        content = {
            Column(
                modifier = Modifier
                    .padding(MEDIUM_PADDING)
            ) {
                Text(
                    text = post.embed_title!!,
                    style = MaterialTheme.typography.subtitle1
                )
                post.embed_description?.also {
                    Text(
                        text = it,
                    )
                }
            }
        }
    )
}

@Composable
fun PostOptionsDialog(
    postView: PostView,
    onDismissRequest: () -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onEditPostClick: () -> Unit = {},
    isCreator: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                // TODO maybe add Go To?
                CommunityLinkLarger(
                    community = postView.community,
                    onClick = onCommunityClick,
                )
                IconAndTextDrawerItem(
                    text = "Copy Permalink",
                    icon = Icons.Default.Link,
                    onClick = {
                        val permalink = postView.post.ap_id
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(ctx, "Permalink Copied", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
                if (isCreator) {
                    IconAndTextDrawerItem(
                        text = "Edit",
                        icon = Icons.Default.Edit,
                        onClick = onEditPostClick,
                    )
                }
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun PostOptionsDialogPreview() {
    PostOptionsDialog(postView = samplePostView, isCreator = true)
}
