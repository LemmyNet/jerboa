package com.jerboa.ui.components.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePostView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PostListings(
  posts: List<PostView>,
  onItemClicked: (postView: PostView) -> Unit = {},
  navController: NavController?,
) {
  // Remember our own LazyListState, can be
  // used to move to any position in the column.
  val listState = rememberLazyListState()

  @OptIn(ExperimentalFoundationApi::class)
  LazyColumn(state = listState) {
    // List of items
    items(posts) { postView ->
      PostListing(
        postView = postView,
        onItemClicked = onItemClicked,
        navController = navController
      )
      
    }
  }
}

@Composable
private fun PostListingsHeader(scope: CoroutineScope, scaffoldState: ScaffoldState) {
  TopAppBar(
    title = {
      Text(
        text = "Top Stories",
      )
    },
    navigationIcon = {
      IconButton(onClick = {
        scope.launch {
          scaffoldState.drawerState.open()
        }
      }) {
        Icon(
          Icons.Filled.Menu,
          contentDescription = "Menu"
        )
      }
    },
  )
}

@Preview
@Composable
fun PreviewPostListingsHeader() {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
  PostListingsHeader(scope, scaffoldState)
}

@Preview
@Composable
fun PreviewPostListings() {
  PostListings(
    posts = listOf(samplePostView, samplePostView),
    onItemClicked = {},
    navController = null,
  )
}

@Composable
fun PostListingsScreen(
  navController: NavController,
  postListingsViewModel: PostListingsViewModel,
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()

  Surface(color = MaterialTheme.colors.background) {
    Scaffold(
      scaffoldState = scaffoldState,
      topBar = {
        PostListingsHeader(scope, scaffoldState)
      },
      drawerContent = {
        Text("Drawer title", modifier = Modifier.padding(16.dp))
        Divider()
        // Drawer items
      },
      content = {
        if (postListingsViewModel.loading) {
          LinearProgressIndicator()
        }
        PostListings(
          posts = postListingsViewModel.posts,
          postListingsViewModel::onPostClicked,
          navController,
        )
      }
    )
  }
}

