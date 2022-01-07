package com.jerboa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jerboa.api.API
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.ui.components.home.HomeActivity
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

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val accounts by accountViewModel.allAccounts.observeAsState()
            val currentAccount = getCurrentAccount(accounts)

            val startRoute = if (currentAccount != null) {
                API.changeLemmyInstance(currentAccount.instance)
                "home"
            } else {
                "login"
            }

//            val startRoute = "home"

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
                        )
                    }
                    composable(route = "home") {
                        HomeActivity(
                            navController = navController,
                            postListingsViewModel = postListingsViewModel,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(
                        route = "post/{postId}",
                    ) {
                        val postId = it.arguments?.getString("postId")!!.toInt()
                        PostActivity(
                            postId = postId,
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
