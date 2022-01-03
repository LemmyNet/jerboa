package com.jerboa.ui.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jerboa.datatypes.Post
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePost
import com.jerboa.datatypes.samplePostView
import com.jerboa.previewLines
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonLink

@Composable
fun PostHeaderLine(postView: PostView) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    CommunityLink(community = postView.community)
    Text(text = "·")
    PersonLink(person = postView.creator)
    Text(text = "·")
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
fun PostTitleAndDesc(post: Post, fullBody: Boolean = false) {
  Column(
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // Title of the post
    Text(text = post.name, style = MaterialTheme.typography.subtitle1)

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
fun PostFooterLine(postView: PostView) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row() {
      CommentCount(comments = postView.counts.comments)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      Upvotes(upvotes = postView.counts.upvotes)
      Downvotes(downvotes = postView.counts.downvotes)
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = "TODO",
      )
      Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = "TODO",
      )
    }
  }
}

@Composable
fun Upvotes(upvotes: Int) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Filled.Add,
      contentDescription = "TODO",
    )
    Text(text = upvotes.toString())
  }
}

@Preview
@Composable
fun UpvotesPreview() {
  Upvotes(upvotes = 31)
}

@Composable
fun Downvotes(downvotes: Int) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Filled.ArrowDropDown,
      contentDescription = "TODO",
    )
    Text(text = downvotes.toString())
  }
}

@Preview
@Composable
fun DownvotesPreview() {
  Downvotes(downvotes = 6)
}

@Composable
fun CommentCount(comments: Int) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Filled.Add,
      contentDescription = "TODO",
    )
    Text(text = "$comments comments")
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
) {
  Card(
    shape = RoundedCornerShape(0.dp),
    modifier = Modifier
    .padding(vertical = 8.dp)
    .clickable {
      onItemClicked(postView)
      navController?.navigate("post")
    }) {
    Box(modifier = Modifier.padding(8.dp)) {
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {

        // Header
        PostHeaderLine(postView = postView)

        //  Title + metadata
        PostTitleAndDesc(post = postView.post, fullBody)

        // Footer bar
        PostFooterLine(postView = postView)
      }
    }
  }
}

@Composable
private fun PostListingHeader() {
  Surface(
    Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colors.background)
  ) {
    Text(
      text = "The Post",
      style = MaterialTheme.typography.h3,
      textAlign = TextAlign.Center
    )
  }
}

@Preview
@Composable
fun PreviewPostListingHeader() {
  PostListingHeader()
}

@Composable
fun PostListingScreen(
  postView: PostView,
) {
  Surface(color = MaterialTheme.colors.background) {
    Scaffold(
      topBar = {
        PostListingHeader()
      },
    ) {
      PostListing(
        postView = postView,
        fullBody = true,
      )
    }
  }
}

@Preview
@Composable
fun PreviewPostListingScreen() {
  PostListingScreen(postView = samplePostView)
}
