package com.jerboa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jerboa.api.API
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.ui.components.comment.CommentReplyActivity
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeActivity
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.home.SplashScreenActivity
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.login.LoginViewModel
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.InboxViewModel
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.private_message.PrivateMessageReplyActivity
import com.jerboa.ui.theme.JerboaTheme
import kotlinx.coroutines.launch

class JerboaApplication : Application() {
    private val database by lazy { AppDB.getDatabase(this) }
    val repository by lazy { AccountRepository(database.accountDao()) }
}

class MainActivity : ComponentActivity() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val postViewModel by viewModels<PostViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val siteViewModel by viewModels<SiteViewModel>()
    private val communityViewModel by viewModels<CommunityViewModel>()
    private val personProfileViewModel by viewModels<PersonProfileViewModel>()
    private val inboxViewModel by viewModels<InboxViewModel>()

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read the accounts from the DB, sync for first load only
        val accounts = accountViewModel.allAccountSync
        val account = getCurrentAccount(accounts)

        setContent {
            val scope = rememberCoroutineScope()

            val startRoute = if (account != null) {
                API.changeLemmyInstance(account.instance)
                siteViewModel.fetchSite(auth = account.jwt)

                // Use to get your users default sort and listing types
                siteViewModel.siteRes?.my_user?.also { myUser ->
                    scope.launch {
                        homeViewModel.fetchPosts(
                            account = account,
                            changeListingType = ListingType.values()[
                                myUser.local_user_view.local_user
                                    .default_listing_type
                            ],
                            changeSortType = SortType.values()[
                                myUser.local_user_view.local_user
                                    .default_sort_type
                            ],
                        )
                    }
                }
                "home"
            } else {
                "login"
            }

            val navController = rememberNavController()

            JerboaTheme {
                NavHost(
                    navController = navController,
                    startDestination = "splashScreen",
                ) {
                    composable(route = "login") {
                        LoginActivity(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            homeViewModel = homeViewModel,
                        )
                    }
                    composable(route = "splashScreen") {
                        SplashScreenActivity(
                            startRoute = startRoute,
                            navController = navController,
                        )
                    }
                    composable(route = "home") {
                        HomeActivity(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            communityViewModel = communityViewModel,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                            inboxViewModel = inboxViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(route = "community") {
                        CommunityActivity(
                            navController = navController,
                            communityViewModel = communityViewModel,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(route = "profile") {
                        PersonProfileActivity(
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                            communityViewModel = communityViewModel,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(route = "inbox") {
                        InboxActivity(
                            navController = navController,
                            inboxViewModel = inboxViewModel,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                            communityViewModel = communityViewModel,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(
                        route = "post",
                    ) {
                        PostActivity(
                            postViewModel = postViewModel,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            communityViewModel = communityViewModel,
                            personProfileViewModel = personProfileViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "commentReply",
                    ) {
                        CommentReplyActivity(
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            personProfileViewModel = personProfileViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "privateMessageReply",
                    ) {
                        PrivateMessageReplyActivity(
                            inboxViewModel = inboxViewModel,
                            accountViewModel = accountViewModel,
                            personProfileViewModel = personProfileViewModel,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}
