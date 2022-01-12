package com.jerboa.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.*
import com.jerboa.datatypes.*
import com.jerboa.ui.components.common.PictrsThumbnailImage
import com.jerboa.ui.components.common.PictrsUrlImage
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.*
import java.net.URL

@Composable
fun PostHeaderLine(
    postView: PostView,
    onCommunityClick: (communityId: Int) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
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
        )
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
fun PostTitleBlock(
    post: Post,
    onPostLinkClick: (url: String) -> Unit = {},
) {
    val ctx = LocalContext.current

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
    val ctx = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        PostTitleBlock(post = post, onPostLinkClick = onPostLinkClick)

        // The desc
        post.body?.also { text ->
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
                            preview = !fullBody,
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
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING)

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
                onClick = { onSaveClick(postView) },
                contentColor = if (postView.saved) {
                    Color.Yellow
                } else {
                    Muted
                },
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
fun PreviewImagePostListing() {
    PostListing(
        postView = sampleImagePostView,
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
    onSaveClick: (postView: PostView) -> Unit = {},
    onCommunityClick: (communityId: Int) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
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
            PostHeaderLine(
                postView = postView,
                onCommunityClick = onCommunityClick,
                onPersonClick = onPersonClick,
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
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onSaveClick = onSaveClick,
                onReplyClick = onReplyClick,
                showReply = showReply,
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
