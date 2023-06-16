package com.jerboa

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import arrow.core.Either
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.db.AppSettingsRepository
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.AppSettingsViewModelFactory
import com.jerboa.ui.components.comment.edit.CommentEditActivity
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyActivity
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.ShowChangelog
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getCurrentAccountSync
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.community.sidebar.CommunitySidebarActivity
import com.jerboa.ui.components.home.*
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.login.LoginViewModel
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.create.CreatePostActivity
import com.jerboa.ui.components.post.create.CreatePostViewModel
import com.jerboa.ui.components.post.edit.PostEditActivity
import com.jerboa.ui.components.post.edit.PostEditViewModel
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyActivity
import com.jerboa.ui.components.report.CreateReportViewModel
import com.jerboa.ui.components.report.comment.CreateCommentReportActivity
import com.jerboa.ui.components.report.post.CreatePostReportActivity
import com.jerboa.ui.components.settings.SettingsActivity
import com.jerboa.ui.components.settings.about.AboutActivity
import com.jerboa.ui.components.settings.account.AccountSettingsActivity
import com.jerboa.ui.components.settings.account.AccountSettingsViewModel
import com.jerboa.ui.components.settings.account.AccountSettingsViewModelFactory
import com.jerboa.ui.components.settings.lookandfeel.LookAndFeelActivity
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
    private val accountSettingsViewModel by viewModels<AccountSettingsViewModel>() {
        AccountSettingsViewModelFactory((application as JerboaApplication).accountRepository)
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory((application as JerboaApplication).accountRepository)
    }
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        AppSettingsViewModelFactory((application as JerboaApplication).appSettingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val accountSync = getCurrentAccountSync(accountViewModel)
        fetchInitialData(accountSync, siteViewModel, homeViewModel)

        setContent {
            val account = getCurrentAccount(accountViewModel)
            val appSettings by appSettingsViewModel.appSettings.observeAsState()

            JerboaTheme(
                appSettings = appSettings,
            ) {
                val navController = rememberNavController()
                val ctx = LocalContext.current

                MarkdownHelper.init(
                    navController,
                    appSettingsViewModel.appSettings.value?.useCustomTabs ?: true,
                    appSettingsViewModel.appSettings.value?.usePrivateTabs ?: false,
                )

                ShowChangelog(appSettingsViewModel = appSettingsViewModel)

                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable(
                        route = "login",
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/login" }
                        },
                    ) {
                        LoginActivity(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            homeViewModel = homeViewModel,
                        )
                    }
                    composable(
                        route = "home",
                    ) {
                        HomeActivity(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                        )
                    }
                    composable(
                        route = "community/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        ),
                    ) {
                        LaunchedEffect(Unit) {
                            val communityId = it.arguments?.getInt("id")!!
                            val idOrName = Either.Left(communityId)

                            communityViewModel.fetchCommunity(
                                idOrName = idOrName,
                                auth = account?.jwt,
                            )

                            communityViewModel.fetchPosts(
                                communityIdOrName = idOrName,
                                account = account,
                                clear = true,
                                ctx = ctx,
                            )
                        }

                        CommunityActivity(
                            navController = navController,
                            communityViewModel = communityViewModel,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            postEditViewModel = postEditViewModel,
                            communityListViewModel = communityListViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            siteViewModel = siteViewModel,
                        )
                    }
                    // Only necessary for community deeplinks
                    composable(
                        route = "{instance}/c/{name}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "{instance}/c/{name}" },
                        ),
                        arguments = listOf(
                            navArgument("name") {
                                type = NavType.StringType
                            },
                            navArgument("instance") {
                                type = NavType.StringType
                            },
                        ),
                    ) {
                        LaunchedEffect(Unit) {
                            val name = it.arguments?.getString("name")!!
                            val instance = it.arguments?.getString("instance")!!
                            val idOrName = Either.Right("$name@$instance")

                            communityViewModel.fetchCommunity(
                                idOrName = idOrName,
                                auth = account?.jwt,
                            )

                            communityViewModel.fetchPosts(
                                communityIdOrName = idOrName,
                                account = account,
                                clear = true,
                                ctx = ctx,
                            )
                        }

                        CommunityActivity(
                            navController = navController,
                            communityViewModel = communityViewModel,
                            communityListViewModel = communityListViewModel,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(
                        route = "profile/{id}?saved={saved}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                            navArgument("saved") {
                                defaultValue = false
                                type = NavType.BoolType
                            },
                        ),
                    ) {
                        val savedMode = it.arguments?.getBoolean("saved")!!

                        LaunchedEffect(Unit) {
                            val personId = it.arguments?.getInt("id")!!
                            val idOrName = Either.Left(personId)

                            personProfileViewModel.fetchPersonDetails(
                                idOrName = idOrName,
                                account = account,
                                clearPersonDetails = true,
                                clearPostsAndComments = true,
                                ctx = ctx,
                                changeSavedOnly = savedMode,
                            )
                        }

                        PersonProfileActivity(
                            savedMode = savedMode,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            siteViewModel = siteViewModel,
                        )
                    }
                    // Necessary for deep links
                    composable(
                        route = "{instance}/u/{name}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "{instance}/u/{name}" },
                        ),
                        arguments = listOf(
                            navArgument("name") {
                                type = NavType.StringType
                            },
                            navArgument("instance") {
                                type = NavType.StringType
                            },
                        ),
                    ) {
                        LaunchedEffect(Unit) {
                            val name = it.arguments?.getString("name")!!
                            val instance = it.arguments?.getString("instance")!!
                            val idOrName = Either.Right("$name@$instance")

                            personProfileViewModel.fetchPersonDetails(
                                idOrName = idOrName,
                                account = account,
                                clearPersonDetails = true,
                                clearPostsAndComments = true,
                                ctx = ctx,
                            )
                        }

                        PersonProfileActivity(
                            savedMode = false,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(
                        route = "communityList?select={select}",
                        arguments = listOf(
                            navArgument("select") {
                                defaultValue = false
                                type = NavType.BoolType
                            },
                        ),
                    ) {
                        // Whenever navigating here, reset the list with your followed communities
                        communityListViewModel.setCommunityListFromFollowed(siteViewModel)

                        CommunityListActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            communityListViewModel = communityListViewModel,
                            selectMode = it.arguments?.getBoolean("select")!!,
                        )
                    }
                    composable(
                        route = "createPost",
                        deepLinks = listOf(
                            navDeepLink { mimeType = "text/plain" },
                            navDeepLink { mimeType = "image/*" },
                        ),
                    ) {
                        val activity = ctx.findActivity()
                        val text = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                        val image =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                activity?.intent?.getParcelableExtra(
                                    Intent.EXTRA_STREAM,
                                    Uri::class.java,
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                activity?.intent?.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                            }
                        // url and body will be empty everytime except when there is EXTRA TEXT in the intent
                        var url = ""
                        var body = ""
                        if (Patterns.WEB_URL.matcher(text).matches()) {
                            url = text
                        } else {
                            body = text
                        }

                        CreatePostActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            createPostViewModel = createPostViewModel,
                            communityListViewModel = communityListViewModel,
                            _url = url,
                            _body = body,
                            _image = image,
                        )
                        activity?.intent?.replaceExtras(Bundle())
                    }
                    composable(
                        route = "inbox",
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/inbox" }
                        },
                    ) {
                        if (account != null) {
                            LaunchedEffect(Unit) {
                                inboxViewModel.fetchReplies(
                                    account = account,
                                    clear = true,
                                    ctx = ctx,
                                )
                                inboxViewModel.fetchPersonMentions(
                                    account = account,
                                    clear = true,
                                    ctx = ctx,
                                )
                                inboxViewModel.fetchPrivateMessages(
                                    account = account,
                                    clear = true,
                                    ctx = ctx,
                                )
                            }
                        }

                        InboxActivity(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                            inboxViewModel = inboxViewModel,
                            accountViewModel = accountViewModel,
                            homeViewModel = homeViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(
                        route = "post/{id}",
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/post/{id}" }
                        },
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        ),
                    ) {
                        LaunchedEffect(Unit) {
                            val postId = it.arguments?.getInt("id")!!
                            postViewModel.fetchPost(
                                id = Either.Left(postId),
                                account = account,
                                clearPost = true,
                                clearComments = true,
                                ctx = ctx,
                            )
                        }
                        PostActivity(
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            navController = navController,
                            showCollapsedCommentContent = appSettings?.showCollapsedCommentContent ?: false,
                            showActionBarByDefault = appSettings?.showCommentActionBarByDefault ?: true,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            onClickSortType = { commentSortType ->
                                val postId = it.arguments?.getInt("id")!!
                                postViewModel.fetchPost(
                                    id = Either.Left(postId),
                                    account = account,
                                    clearPost = false,
                                    clearComments = true,
                                    ctx = ctx,
                                    changeSortType = commentSortType,
                                )
                            },
                            selectedSortType = postViewModel.sortType.value,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                        )
                    }
                    composable(
                        route = "comment/{id}",
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/comment/{id}" }
                        },
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        ),
                    ) {
                        LaunchedEffect(Unit) {
                            val commentId = it.arguments?.getInt("id")!!
                            postViewModel.fetchPost(
                                id = Either.Right(commentId),
                                account = account,
                                clearPost = true,
                                clearComments = true,
                                ctx = ctx,
                            )
                        }
                        PostActivity(
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            navController = navController,
                            showCollapsedCommentContent = appSettings?.showCollapsedCommentContent ?: false,
                            showActionBarByDefault = appSettings?.showCommentActionBarByDefault ?: true,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
                            onClickSortType = { commentSortType ->
                                val commentId = it.arguments?.getInt("id")!!
                                postViewModel.fetchPost(
                                    id = Either.Right(commentId),
                                    account = account,
                                    clearPost = false,
                                    clearComments = true,
                                    ctx = ctx,
                                    changeSortType = commentSortType,
                                )
                            },
                            selectedSortType = postViewModel.sortType.value,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                        )
                    }
                    composable(
                        route = "commentReply",
                    ) {
                        CommentReplyActivity(
                            commentReplyViewModel = commentReplyViewModel,
                            postViewModel = postViewModel,
                            accountViewModel = accountViewModel,
                            personProfileViewModel = personProfileViewModel,
                            navController = navController,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(
                        route = "siteSidebar",
                    ) {
                        SiteSidebarActivity(
                            siteViewModel = siteViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "communitySidebar",
                    ) {
                        CommunitySidebarActivity(
                            communityViewModel = communityViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "commentEdit",
                    ) {
                        CommentEditActivity(
                            commentEditViewModel = commentEditViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                        )
                    }
                    composable(
                        route = "postEdit",
                    ) {
                        PostEditActivity(
                            postEditViewModel = postEditViewModel,
                            communityViewModel = communityViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                            homeViewModel = homeViewModel,
                        )
                    }
                    composable(
                        route = "privateMessageReply",
                    ) {
                        PrivateMessageReplyActivity(
                            inboxViewModel = inboxViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                            siteViewModel = siteViewModel,
                        )
                    }
                    composable(
                        route = "commentReport/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        ),
                    ) {
                        createReportViewModel.setCommentId(it.arguments?.getInt("id")!!)
                        CreateCommentReportActivity(
                            createReportViewModel = createReportViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "postReport/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        ),
                    ) {
                        createReportViewModel.setPostId(it.arguments?.getInt("id")!!)
                        CreatePostReportActivity(
                            createReportViewModel = createReportViewModel,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }
                    composable(
                        route = "settings",
                    ) {
                        SettingsActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                        )
                    }
                    composable(
                        route = "lookAndFeel",
                    ) {
                        LookAndFeelActivity(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(
                        route = "accountSettings",
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/settings" }
                        },
                    ) {
                        AccountSettingsActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            accountSettingsViewModel = accountSettingsViewModel,
                        )
                    }
                    composable(
                        route = "about",
                    ) {
                        AboutActivity(
                            navController = navController,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                        )
                    }
                }
            }
        }
    }
}
