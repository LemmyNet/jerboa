package com.jerboa

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jerboa.api.API
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.ui.components.home.LoginScreen
import com.jerboa.ui.components.home.UserViewModel
import com.jerboa.ui.components.post.PostListingScreen
import com.jerboa.ui.components.post.PostListingsScreen
import com.jerboa.ui.components.post.PostListingsViewModel
import com.jerboa.ui.theme.JerboaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class JerboaApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDB.getDatabase(this, applicationScope) }
    val repository by lazy { AccountRepository(database.accountDao()) }
}

class MainActivity : ComponentActivity() {

    private val postListingsViewModel by viewModels<PostListingsViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Log.e("jerboa", "got here")

            val navController = rememberNavController()
            val ctx = LocalContext.current

            val accounts by accountViewModel.allAccounts.observeAsState()
            val currentAccount = getCurrentAccount(accountViewModel)

            val startRoute = if (currentAccount != null) {
                API.setInstance(currentAccount.instance)

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
                            userViewModel = userViewModel,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(route = "home") {
                        PostListingsScreen(
                            navController = navController,
                            postListingsViewModel = postListingsViewModel,
                            accountViewModel = accountViewModel,
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
