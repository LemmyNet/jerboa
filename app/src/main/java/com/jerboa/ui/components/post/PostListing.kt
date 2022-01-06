package com.jerboa.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.api.API
import com.jerboa.datatypes.Post
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.CreatePostLike
import com.jerboa.datatypes.samplePost
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.previewLines
import com.jerboa.toastException
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonLink
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.voteColor
import kotlinx.coroutines.launch

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

@Composable
fun DotSpacer() {
    Text(
        text = "·",
        modifier = Modifier.padding(horizontal = 8.dp)
    )
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
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Title of the post
        Text(
            text = post.name,
            style = MaterialTheme.typography.subtitle1
        )

        // The desc
        post.body?.let {
            val text = if (fullBody) it else previewLines(it)
            // TODO markdown
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
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
    accountViewModel: AccountViewModel = viewModel(),
) {
    val acct = getCurrentAccount(accountViewModel = accountViewModel)
    val ctx = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            CommentCount(comments = postView.counts.comments)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Upvotes(
                postView = postView,
                auth = acct?.jwt,
            )
            Downvotes(postView = postView, auth = acct?.jwt)
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
fun Upvotes(postView: PostView, auth: String?) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var upvotes by remember { mutableStateOf(postView.counts.upvotes) }
    var myVote by remember { mutableStateOf(postView.my_vote) }
    val voteColor = voteColor(myVote = myVote)

    Row(
        verticalAlignment = Alignment.CenterVertically,

        modifier = Modifier.clickable {
            auth?.let {
                coroutineScope.launch {
                    try {
                        val newVote = if (myVote == 1) {
                            0
                        } else {
                            1
                        }
                        myVote = newVote
                        val form = CreatePostLike(
                            post_id = postView.post.id, score = newVote, auth = it
                        )
                        val post = API.getInstance().likePost(form)
                        upvotes = post.post_view.counts.upvotes
                    } catch (e: Exception) {
                        toastException(ctx = ctx, error = e)
                    }
                }
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            tint = voteColor,
            contentDescription = "TODO",
            modifier = Modifier
                .size(ACTION_BAR_ICON_SIZE)
                .padding(end = 2.dp)

        )
        Text(
            text = upvotes.toString(),
            style = MaterialTheme.typography.button,
            color = voteColor,
        )
    }
}

@Preview
@Composable
fun UpvotesPreview() {
    Upvotes(postView = samplePostView, auth = null)
}

@Composable
fun Downvotes(postView: PostView, auth: String?) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var downvotes by remember { mutableStateOf(postView.counts.downvotes) }
    var myVote by remember { mutableStateOf(postView.my_vote) }
    val voteColor = voteColor(myVote = myVote)

    Row(
        verticalAlignment = Alignment.CenterVertically,

        modifier = Modifier.clickable {
            auth?.let {
                coroutineScope.launch {
                    try {
                        val newVote = if (myVote == -1) {
                            0
                        } else {
                            -1
                        }
                        myVote = newVote
                        val form = CreatePostLike(
                            post_id = postView.post.id, score = newVote, auth = it
                        )
                        val post = API.getInstance().likePost(form)
                        downvotes = post.post_view.counts.downvotes
                    } catch (e: Exception) {
                        toastException(ctx = ctx, error = e)
                    }
                }
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            tint = voteColor,
            contentDescription = "TODO",
            modifier = Modifier
                .size(ACTION_BAR_ICON_SIZE)
                .padding(end = 2.dp)

        )
        Text(
            text = downvotes.toString(),
            style = MaterialTheme.typography.button,
            color = voteColor,
        )
    }
}

@Preview
@Composable
fun DownvotesPreview() {
    Downvotes(postView = samplePostView, auth = null)
}

@Composable
fun CommentCount(comments: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubble,
//      painter = painterResource(id = R.drawable.ic_message_square),
            contentDescription = "TODO",
            modifier = Modifier
                .size(ACTION_BAR_ICON_SIZE)
                .padding(end = 2.dp)
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
    onItemClicked: (postView: PostView) -> Unit = {},
    navController: NavController? = null,
    accountViewModel: AccountViewModel = viewModel(),
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable {
                onItemClicked(postView)
                navController?.navigate("post")
            }
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Header
                PostHeaderLine(postView = postView)

                //  Title + metadata
                PostTitleAndDesc(post = postView.post, fullBody)

                // Footer bar
                PostFooterLine(postView = postView, accountViewModel = accountViewModel)
            }
        }
    }
}

@Composable
private fun PostListingHeader(
    navController: NavController,
) {
    TopAppBar(
        title = {
            Text(
                text = "Post",
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
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
    PostListingHeader(navController = navController)
}

@Composable
fun PostListingScreen(
    postView: PostView,
    navController: NavController,
    accountViewModel: AccountViewModel = viewModel(),
) {
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                PostListingHeader(navController)
            },
        ) {
            PostListing(
                postView = postView,
                fullBody = true,
                accountViewModel = accountViewModel,
            )
        }
    }
}

@Preview
@Composable
fun PreviewPostListingScreen() {
    val navController = rememberNavController()
    PostListingScreen(
        postView = samplePostView,
        navController = navController
    )
}
