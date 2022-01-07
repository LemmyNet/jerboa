package com.jerboa.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.*
import com.jerboa.datatypes.Post
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePost
import com.jerboa.datatypes.samplePostView
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonLink
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PostHeaderLine(postView: PostView) {
    FlowRow(
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        CommunityLink(community = postView.community)
        DotSpacer()
        PersonLink(person = postView.creator)
        DotSpacer()
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
fun PostTitleAndDesc(
    post: Post,
    fullBody: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
    ) {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.subtitle1
        )

        // The desc
        post.body?.let {
            val text = if (fullBody) it else previewLines(it)
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(MEDIUM_PADDING)
                    .fillMaxWidth(),
                backgroundColor = colorShade(MaterialTheme.colors.surface, 2.5f),
                content = {
                    MyMarkdownText(
                        markdown = text,
                        modifier = Modifier.padding(MEDIUM_PADDING)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewStoryTitleAndMetadata() {
    PostTitleAndDesc(
        post = samplePost
    )
}

@Composable
fun PostFooterLine(
    postView: PostView,
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SMALL_PADDING)
    ) {
        Row {
            CommentCount(comments = postView.counts.comments)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            verticalAlignment = Alignment.CenterVertically
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
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "TODO",
                modifier = Modifier.size(ACTION_BAR_ICON_SIZE),
            )
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "TODO",
                modifier = Modifier.size(ACTION_BAR_ICON_SIZE)
            )
        }
    }
}

@Composable
fun CommentCount(comments: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubble,
            contentDescription = "TODO",
            modifier = Modifier
                .size(ACTION_BAR_ICON_SIZE)
                .padding(end = SMALL_PADDING)
        )
        Text(
            text = "$comments comments",
        )
    }
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

@Composable
fun PostListing(
    postView: PostView,
    fullBody: Boolean = false,
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    navController: NavController? = null,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(vertical = MEDIUM_PADDING)
            .clickable {
                navController?.navigate("post/${postView.post.id}")
            }
    ) {
        Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
            ) {

                // Header
                PostHeaderLine(postView = postView)

                //  Title + metadata
                PostTitleAndDesc(post = postView.post, fullBody)

                // Footer bar
                PostFooterLine(
                    postView = postView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                )
            }
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
