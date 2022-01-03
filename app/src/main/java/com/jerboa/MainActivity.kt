package com.jerboa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.ui.components.post.PostListingScreen
import com.jerboa.ui.components.post.PostListingsScreen
import com.jerboa.ui.components.post.PostListingsViewModel
import com.jerboa.ui.theme.JerboaTheme

class MainActivity : ComponentActivity() {

  private val postListingsViewModel by viewModels<PostListingsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val navController = rememberNavController()

      // Fetch initial posts for home screen
      postListingsViewModel.fetchPosts(GetPosts())

      JerboaTheme {
        NavHost(navController = navController, startDestination = "home") {
          composable("home") {
            PostListingsScreen(navController, postListingsViewModel)
          }
          composable("post") {
            PostListingScreen(
              postListingsViewModel.clickedPost,
            )
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String) {
  Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  JerboaTheme {
    Greeting("Android")
  }
}
