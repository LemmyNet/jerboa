package com.jerboa

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jerboa.api.API
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPost
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.ui.components.comment.CommentReplyActivity
import com.jerboa.ui.components.home.HomeActivity
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.login.LoginViewModel
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.PostListingsViewModel
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.theme.JerboaTheme

class JerboaApplication : Application() {
    val database by lazy { AppDB.getDatabase(this) }
    val repository by lazy { AccountRepository(database.accountDao()) }
}

class MainActivity : ComponentActivity() {

    private val postListingsViewModel by viewModels<PostListingsViewModel>()
    private val postViewModel by viewModels<PostViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val siteViewModel by viewModels<SiteViewModel>()

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Read the accounts from the DB
            val accounts by accountViewModel.allAccounts.observeAsState()
            val account = getCurrentAccount(accounts)

            val startRoute = if (account != null) {
                API.changeLemmyInstance(account.instance)
                siteViewModel.fetchSite(auth = account.jwt)

                // Use to get your users default sort and listing types
                siteViewModel.siteRes?.my_user?.also { myUser ->
                    LaunchedEffect(Unit, block = {

                        postListingsViewModel.fetchPosts(
                            auth = account.jwt,
                            changeListingType = ListingType.values()[
                                myUser.local_user_view.local_user
                                    .default_listing_type
                            ],
                            changeSortType = SortType.values()[
                                myUser.local_user_view.local_user
                                    .default_sort_type
                            ]
                        )
                    })
                }
                "home"
            } else {
                "login"
            }

//            val startRoute = "home"

            val navController = rememberNavController()

            JerboaTheme {
                NavHost(
                    navController = navController,
                    startDestination = startRoute,
                ) {
                    composable(route = "login") {
                        LoginActivity(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(route = "home") {
                        HomeActivity(
                            navController = navController,
                            postListingsViewModel = postListingsViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            isScrolledToEnd = {
                                postListingsViewModel.fetchPosts(
                                    auth = account?.jwt,
                                    nextPage = true,
                                )
                            },
                        )
                    }
                    composable(
                        route = "post/{postId}?fetch={fetch}",
                        arguments = listOf(
                            navArgument("postId") {
                                type = NavType.IntType
                            },
                            navArgument("fetch") {
                                defaultValue = false
                                type = NavType.BoolType
                            }
                        )
                    ) {
                        val postId = it.arguments?.getInt("postId")!!

                        LaunchedEffect(Unit, block = {
                            val fetch = it.arguments?.getBoolean("fetch")!!
                            Log.d("jerboa", "fetch = $fetch")

                            if (fetch) {
                                postViewModel.postView = null
                                postViewModel.fetchPost(
                                    GetPost(
                                        id = postId,
                                        auth = account?.jwt,
                                    )
                                )
                            }
                        })

                        PostActivity(
                            postId = postId,
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "commentReply",
                    ) {
                        CommentReplyActivity(
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}
