package com.jerboa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jerboa.api.API
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.AppDB
import com.jerboa.ui.components.home.LoginScreen
import com.jerboa.ui.components.home.UserViewModel
import com.jerboa.ui.components.post.PostListingScreen
import com.jerboa.ui.components.post.PostListingsScreen
import com.jerboa.ui.components.post.PostListingsViewModel
import com.jerboa.ui.theme.JerboaTheme

class MainActivity : ComponentActivity() {

    private val postListingsViewModel by viewModels<PostListingsViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val ctx = LocalContext.current

            val db = AppDB.getInstance(ctx)
            val currentAccount = db.accountDao().getSelected()
            val startRoute = if (currentAccount != null) {
                API.setInstance(currentAccount.instance)
                userViewModel.setTheAccount(currentAccount)

                // Fetch initial posts for home screen
                postListingsViewModel.fetchPosts(
                    GetPosts(
                        auth = currentAccount.jwt
                    )
                )
                "home"
            } else {
                "login"
            }

            JerboaTheme {
                NavHost(
                    navController = navController,
                    startDestination = startRoute,
                ) {
                    composable(route = "login") {
                        LoginScreen(
                            navController = navController,
                            userViewModel = userViewModel
                        )
                    }
                    composable(route = "home") {
                        PostListingsScreen(
                            navController = navController,
                            postListingsViewModel = postListingsViewModel,
                            userViewModel = userViewModel,
                        )
                    }
                    composable(route = "post") {
                        PostListingScreen(
                            navController = navController,
                            postView = postListingsViewModel.clickedPost,
                        )
                    }
                }
            }
        }
    }
}
