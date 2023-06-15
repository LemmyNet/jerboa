package com.jerboa

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.db.AppSettingsRepository
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.AppSettingsViewModelFactory
import com.jerboa.nav.aboutScreen
import com.jerboa.nav.accountSettingsScreen
import com.jerboa.nav.commentEditScreen
import com.jerboa.nav.commentReplyScreen
import com.jerboa.nav.commentReportScreen
import com.jerboa.nav.commentScreen
import com.jerboa.nav.communityListScreen
import com.jerboa.nav.communityScreen
import com.jerboa.nav.communityScreenFromUrl
import com.jerboa.nav.communitySideBarScreen
import com.jerboa.nav.createPostScreen
import com.jerboa.nav.defaultEnterTransition
import com.jerboa.nav.defaultExitTransition
import com.jerboa.nav.defaultPopEnterTransition
import com.jerboa.nav.defaultPopExitTransition
import com.jerboa.nav.homeRoutePattern
import com.jerboa.nav.homeScreen
import com.jerboa.nav.inboxScreen
import com.jerboa.nav.loginScreen
import com.jerboa.nav.lookAndFeelScreen
import com.jerboa.nav.postEditScreen
import com.jerboa.nav.postReportScreen
import com.jerboa.nav.postScreen
import com.jerboa.nav.privateMessageReplyScreen
import com.jerboa.nav.profileScreen
import com.jerboa.nav.profileScreenFromUrl
import com.jerboa.nav.settingsScreen
import com.jerboa.nav.siteSideBarScreen
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.ShowChangelog
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getCurrentAccountSync
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.*
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.login.LoginViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.create.CreatePostViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel
import com.jerboa.ui.components.report.CreateReportViewModel
import com.jerboa.ui.components.settings.account.AccountSettingsViewModel
import com.jerboa.ui.components.settings.account.AccountSettingsViewModelFactory
import com.jerboa.ui.theme.JerboaTheme

class JerboaApplication : Application() {
    private val database by lazy { AppDB.getDatabase(this) }
    val accountRepository by lazy { AccountRepository(database.accountDao()) }
    val appSettingsRepository by lazy { AppSettingsRepository(database.appSettingsDao()) }
}

class MainActivity : ComponentActivity() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val postViewModel by viewModels<PostViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val siteViewModel by viewModels<SiteViewModel>()
    private val communityViewModel by viewModels<CommunityViewModel>()
    private val personProfileViewModel by viewModels<PersonProfileViewModel>()
    private val inboxViewModel by viewModels<InboxViewModel>()
    private val communityListViewModel by viewModels<CommunityListViewModel>()
    private val createPostViewModel by viewModels<CreatePostViewModel>()
    private val commentReplyViewModel by viewModels<CommentReplyViewModel>()
    private val commentEditViewModel by viewModels<CommentEditViewModel>()
    private val postEditViewModel by viewModels<PostEditViewModel>()
    private val createReportViewModel by viewModels<CreateReportViewModel>()
    private val accountSettingsViewModel by viewModels<AccountSettingsViewModel> {
        AccountSettingsViewModelFactory((application as JerboaApplication).accountRepository)
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).accountRepository)
    }
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        AppSettingsViewModelFactory((application as JerboaApplication).appSettingsRepository)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MarkdownHelper.init(
            this,
            appSettingsViewModel.appSettings.value?.useCustomTabs ?: true,
            appSettingsViewModel.appSettings.value?.usePrivateTabs ?: false,
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val accountSync = getCurrentAccountSync(accountViewModel)
        fetchInitialData(accountSync, siteViewModel, homeViewModel)

        setContent {
            val account = getCurrentAccount(accountViewModel)
            val appSettings by appSettingsViewModel.appSettings.observeAsState()

            JerboaTheme(
                appSettings = appSettings,
            ) {
                val navController = rememberAnimatedNavController()
                val ctx = LocalContext.current

                ShowChangelog(appSettingsViewModel = appSettingsViewModel)

                AnimatedNavHost(
                    navController = navController,
                    startDestination = homeRoutePattern,
                    enterTransition = { defaultEnterTransition },
                    exitTransition = { defaultExitTransition },
                    popEnterTransition = { defaultPopEnterTransition },
                    popExitTransition = { defaultPopExitTransition },
                ) {
                    loginScreen(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        accountViewModel = accountViewModel,
                        siteViewModel = siteViewModel,
                        homeViewModel = homeViewModel,
                    )

                    homeScreen(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        accountViewModel = accountViewModel,
                        siteViewModel = siteViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        communityListViewModel = communityListViewModel,
                        inboxViewModel = inboxViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        personProfileViewModel = personProfileViewModel,
                        commentEditViewModel = commentEditViewModel,
                        appSettings = appSettings,
                    )

                    communityScreen(
                        navController = navController,
                        communityViewModel = communityViewModel,
                        accountViewModel = accountViewModel,
                        postEditViewModel = postEditViewModel,
                        communityListViewModel = communityListViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                        ctx = ctx,
                    )

                    communityScreenFromUrl(
                        navController = navController,
                        communityViewModel = communityViewModel,
                        accountViewModel = accountViewModel,
                        postEditViewModel = postEditViewModel,
                        communityListViewModel = communityListViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                        ctx = ctx,
                    )

                    profileScreen(
                        navController = navController,
                        personProfileViewModel = personProfileViewModel,
                        accountViewModel = accountViewModel,
                        commentEditViewModel = commentEditViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                    )

                    profileScreenFromUrl(
                        navController = navController,
                        personProfileViewModel = personProfileViewModel,
                        accountViewModel = accountViewModel,
                        commentEditViewModel = commentEditViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                        ctx = ctx,
                    )

                    communityListScreen(
                        navController = navController,
                        accountViewModel = accountViewModel,
                        communityListViewModel = communityListViewModel,
                        siteViewModel = siteViewModel,
                    )

                    createPostScreen(
                        navController = navController,
                        accountViewModel = accountViewModel,
                        createPostViewModel = createPostViewModel,
                        communityListViewModel = communityListViewModel,
                        ctx = ctx,
                    )

                    inboxScreen(
                        navController = navController,
                        inboxViewModel = inboxViewModel,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        ctx = ctx,
                    )

                    postScreen(
                        navController = navController,
                        postViewModel = postViewModel,
                        accountViewModel = accountViewModel,
                        commentEditViewModel = commentEditViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                        ctx = ctx,
                    )

                    commentScreen(
                        navController = navController,
                        postViewModel = postViewModel,
                        accountViewModel = accountViewModel,
                        commentEditViewModel = commentEditViewModel,
                        commentReplyViewModel = commentReplyViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        siteViewModel = siteViewModel,
                        account = account,
                        appSettings = appSettings,
                        ctx = ctx,
                    )

                    commentReplyScreen(
                        commentReplyViewModel = commentReplyViewModel,
                        postViewModel = postViewModel,
                        accountViewModel = accountViewModel,
                        personProfileViewModel = personProfileViewModel,
                        navController = navController,
                        siteViewModel = siteViewModel,
                    )

                    siteSideBarScreen(
                        siteViewModel = siteViewModel,
                        navController = navController,
                    )

                    communitySideBarScreen(
                        communityViewModel = communityViewModel,
                        navController = navController,
                    )

                    commentEditScreen(
                        commentEditViewModel = commentEditViewModel,
                        accountViewModel = accountViewModel,
                        navController = navController,
                        personProfileViewModel = personProfileViewModel,
                        postViewModel = postViewModel,
                    )

                    postEditScreen(
                        postEditViewModel = postEditViewModel,
                        communityViewModel = communityViewModel,
                        accountViewModel = accountViewModel,
                        navController = navController,
                        personProfileViewModel = personProfileViewModel,
                        postViewModel = postViewModel,
                        homeViewModel = homeViewModel,
                    )

                    privateMessageReplyScreen(
                        inboxViewModel = inboxViewModel,
                        accountViewModel = accountViewModel,
                        navController = navController,
                        siteViewModel = siteViewModel,
                    )

                    commentReportScreen(
                        createReportViewModel = createReportViewModel,
                        accountViewModel = accountViewModel,
                        navController = navController,
                    )

                    postReportScreen(
                        createReportViewModel = createReportViewModel,
                        accountViewModel = accountViewModel,
                        navController = navController,
                    )

                    settingsScreen(
                        navController = navController,
                        accountViewModel = accountViewModel,
                    )

                    lookAndFeelScreen(
                        navController = navController,
                        appSettingsViewModel = appSettingsViewModel,
                    )

                    accountSettingsScreen(
                        navController = navController,
                        accountViewModel = accountViewModel,
                        siteViewModel = siteViewModel,
                        accountSettingsViewModel = accountSettingsViewModel,
                    )

                    aboutScreen(
                        navController = navController,
                        appSettings = appSettings,
                    )
                }
            }
        }
    }
}
