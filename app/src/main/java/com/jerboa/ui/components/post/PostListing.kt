package com.jerboa.ui.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.*
import com.jerboa.R
import com.jerboa.datatypes.*
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonLink
import com.jerboa.ui.theme.*
import java.net.URL

@Composable
fun PostHeaderLine(postView: PostView) {
    FlowRow(
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        CommunityLink(community = postView.community)
        DotSpacer()
        PersonLink(person = postView.creator)
        DotSpacer()
        postView.post.url?.also {
            // TODO hide also if its the same instance / domain
            Text(text = URL(it).host, color = Muted)
            DotSpacer()
        }
        TimeAgo(dateStr = postView.post.published)
    }
}

@Preview
@Composable
fun PostHeaderLinePreview() {
    val postView = samplePostView
    PostHeaderLine(postView = postView)
}

@Composable
fun PostNodeHeader(postView: PostView) {
    CommentOrPostNodeHeader(
        creator = postView.creator,
        score = postView.counts.score,
        myVote = postView.my_vote,
        published = postView.post.published
    )
}

@Composable
fun PostTitleAndImage(
    post: Post,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    val ctx = LocalContext.current

    Row {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.weight(1f)
        )

        post.url?.also { url ->

            MaterialTheme.colors
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
                Image(
                    painter = rememberImagePainter(
                        data = thumbnail,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_foreground)
                            transformations(RoundedCornersTransformation(12f))
                        },
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
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
    val ctx = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        PostTitleAndImage(post = post, onPostLinkClick = onPostLinkClick)

        // The desc
        post.body?.also { text ->
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(MEDIUM_PADDING)
                    .fillMaxWidth(),
                backgroundColor = colorShade(MaterialTheme.colors.surface, 2.5f),
                content = {
                    MyMarkdownText(
                        markdown = text,
                        modifier = Modifier
                            .padding(MEDIUM_PADDING),
                        preview = !fullBody,
                    )
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
    showReply: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row {
            CommentCount(comments = postView.counts.comments)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)

        ) {
            VoteGeneric(
                myVote = postView.my_vote,
                votes = postView.counts.upvotes, item = postView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
            )
            VoteGeneric(
                myVote = postView.my_vote,
                votes = postView.counts.downvotes, item = postView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
            )
            ActionBarButton(
                icon = Icons.Default.Star,
            )
            if (showReply) {
                ActionBarButton(
                    icon = Icons.Default.Reply,
                    onClick = { onReplyClick(postView) },
                )
            }
            ActionBarButton(
                icon = Icons.Default.MoreVert,
            )
        }
    }
}

@Composable
fun CommentCount(comments: Int) {
    ActionBarButton(
        icon = Icons.Default.ChatBubble,
        text = "$comments comments",
        noClick = true,
    )
}

@Preview
@Composable
fun CommentCountPreview() {
    CommentCount(42)
}

@Preview
@Composable
fun PostFooterLinePreview() {
    PostFooterLine(postView = samplePostView)
}

@Preview
@Composable
fun PreviewPostListing() {
    PostListing(
        postView = samplePostView,
        fullBody = true,
    )
}

@Preview
@Composable
fun PreviewLinkPostListing() {
    PostListing(
        postView = sampleLinkPostView,
        fullBody = true,
    )
}

@Preview
@Composable
fun PreviewLinkNoThumbnailPostListing() {
    PostListing(
        postView = sampleLinkNoThumbnailPostView,
        fullBody = true,
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
    showReply: Boolean = false,
) {
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
            PostHeaderLine(postView = postView)

            //  Title + metadata
            PostBody(
                post = postView.post,
                fullBody,
                onPostLinkClick = onPostLinkClick,
            )

            // Footer bar
            PostFooterLine(
                postView = postView,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onReplyClick = onReplyClick,
                showReply = showReply,
            )
        }
    }
}

@Composable
fun PostListingHeader(
    navController: NavController,
) {
    TopAppBar(
        title = {
            Text(
                text = "Post",
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Preview
@Composable
fun PostListingHeaderPreview() {
    val navController = rememberNavController()
    PostListingHeader(navController)
}
