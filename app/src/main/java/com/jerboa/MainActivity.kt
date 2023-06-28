package com.jerboa

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import arrow.core.Either
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.MINIMUM_API_VERSION
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.AccountRepository
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AccountViewModelFactory
import com.jerboa.db.AppDB
import com.jerboa.db.AppSettingsRepository
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.AppSettingsViewModelFactory
import com.jerboa.ui.components.comment.edit.CommentEditActivity
import com.jerboa.ui.components.comment.reply.CommentReplyActivity
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.CommentEditDeps
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.PostEditDeps
import com.jerboa.ui.components.common.PrivateMessageDeps
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.common.ShowChangelog
import com.jerboa.ui.components.common.ShowOutdatedServerDialog
import com.jerboa.ui.components.common.SwipeToNavigateBack
import com.jerboa.ui.components.common.getCurrentAccountSync
import com.jerboa.ui.components.common.takeDepsFromRoot
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.sidebar.CommunitySidebarActivity
import com.jerboa.ui.components.home.BottomNavActivity
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.home.sidebar.SiteSidebarActivity
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.create.CreatePostActivity
import com.jerboa.ui.components.post.edit.PostEditActivity
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyActivity
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

class MainActivity : AppCompatActivity() {
    private val siteViewModel by viewModels<SiteViewModel>()
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

        val accountSync = getCurrentAccountSync(accountViewModel)

        setContent {
            val ctx = LocalContext.current

            API.errorHandler = {
                Log.e("jerboa", it.toString())
                runOnUiThread {
                    Toast.makeText(
                        ctx,
                        ctx.resources.getString(R.string.networkError),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                null
            }

            LaunchedEffect(Unit) {
                fetchInitialData(accountSync, siteViewModel)
            }

            val appSettings by appSettingsViewModel.appSettings.observeAsState(APP_SETTINGS_DEFAULT)

            JerboaTheme(
                appSettings = appSettings,
            ) {
                val navController = rememberAnimatedNavController()
                val serverVersionOutdatedViewed = remember { mutableStateOf(false) }

                MarkdownHelper.init(
                    navController,
                    appSettings.useCustomTabs,
                    appSettings.usePrivateTabs,
                )

                ShowChangelog(appSettingsViewModel = appSettingsViewModel)

                when (val siteRes = siteViewModel.siteRes) {
                    is ApiState.Success -> {
                        val siteVersion = siteRes.data.version
                        if (compareVersions(siteVersion, MINIMUM_API_VERSION) < 0 && !serverVersionOutdatedViewed.value) {
                            ShowOutdatedServerDialog(
                                siteVersion = siteVersion,
                                onConfirm = { serverVersionOutdatedViewed.value = true },
                            )
                        }
                    }
                    else -> {}
                }

                AnimatedNavHost(
                    route = Route.Graph.ROOT,
                    navController = navController,
                    startDestination = Route.HOME,
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { -it } },
                    popEnterTransition = { slideInHorizontally { -it } },
                    popExitTransition = { slideOutHorizontally { it } },
                ) {
                    composable(
                        route = Route.LOGIN,
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/login" }
                        },
                    ) {
                        LoginActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.HOME) {
                        BottomNavActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            appSettings = appSettings,
                        )
                    }

                    navigation(
                        route = Route.Graph.COMMUNITY,
                        startDestination = Route.COMMUNITY_FROM_ID,
                    ) {
                        composable(
                            route = Route.COMMUNITY_FROM_ID,
                            arguments = listOf(
                                navArgument(Route.CommunityFromIdArgs.ID) {
                                    type = Route.CommunityFromIdArgs.ID_TYPE
                                },
                            ),
                        ) {
                            val args = Route.CommunityFromIdArgs(it)
                            val communityViewModel: CommunityViewModel = viewModel(
                                remember(it) { navController.getBackStackEntry(Route.Graph.COMMUNITY) },
                            )

                            CommunityActivity(
                                communityArg = Either.Left(args.id),
                                navController = navController,
                                communityViewModel = communityViewModel,
                                accountViewModel = accountViewModel,
                                appSettingsViewModel = appSettingsViewModel,
                                showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                siteViewModel = siteViewModel,
                                useCustomTabs = appSettings.useCustomTabs,
                                usePrivateTabs = appSettings.usePrivateTabs,
                                blurNSFW = appSettings.blurNSFW,
                            )
                        }

                        // Only necessary for community deeplinks
                        composable(
                            route = Route.COMMUNITY_FROM_URL,
                            arguments = listOf(
                                navArgument(Route.CommunityFromUrlArgs.NAME) {
                                    type = Route.CommunityFromUrlArgs.NAME_TYPE
                                },
                                navArgument(Route.CommunityFromUrlArgs.INSTANCE) {
                                    type = Route.CommunityFromUrlArgs.INSTANCE_TYPE
                                },
                            ),
                        ) {
                            val args = Route.CommunityFromUrlArgs(it)
                            val communityViewModel: CommunityViewModel = viewModel(
                                remember(it) { navController.getBackStackEntry(Route.Graph.COMMUNITY) },
                            )

                            val qualifiedName = "${args.name}@${args.instance}"
                            CommunityActivity(
                                communityArg = Either.Right(qualifiedName),
                                navController = navController,
                                communityViewModel = communityViewModel,
                                accountViewModel = accountViewModel,
                                appSettingsViewModel = appSettingsViewModel,
                                showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                siteViewModel = siteViewModel,
                                useCustomTabs = appSettings.useCustomTabs,
                                usePrivateTabs = appSettings.usePrivateTabs,
                                blurNSFW = appSettings.blurNSFW,
                            )
                        }

                        composable(route = Route.COMMUNITY_SIDEBAR) {
                            val communityViewModel: CommunityViewModel = viewModel(
                                remember(it) { navController.getBackStackEntry(Route.Graph.COMMUNITY) },
                            )

                            CommunitySidebarActivity(
                                communityViewModel = communityViewModel,
                                navController = navController,
                            )
                        }
                    }

                    // TODO: Make community side bar a tab in the community activity so they can
                    //  reuse the same community view model.
                    // HACK: Deep links to nested composables isn't allowed. This hack is required
                    //  open community url from outside the application. This hack is NOT required
                    //  for opening urls within the app itself.
                    composable(
                        route = "redirect/${Route.COMMUNITY_FROM_URL}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = Route.COMMUNITY_FROM_URL },
                        ),
                        arguments = listOf(
                            navArgument(Route.CommunityFromUrlArgs.NAME) {
                                type = Route.CommunityFromUrlArgs.NAME_TYPE
                            },
                            navArgument(Route.CommunityFromUrlArgs.INSTANCE) {
                                type = Route.CommunityFromUrlArgs.INSTANCE_TYPE
                            },
                        ),
                    ) {
                        val args = Route.CommunityFromUrlArgs(it)
                        val route = Route.CommunityFromUrlArgs.makeRoute(
                            instance = args.instance,
                            name = args.name,
                        )
                        navController.navigate(route)
                    }

                    composable(
                        route = Route.PROFILE_FROM_ID,
                        arguments = listOf(
                            navArgument(Route.ProfileFromIdArgs.ID) {
                                type = Route.ProfileFromIdArgs.ID_TYPE
                            },
                            navArgument(Route.ProfileFromIdArgs.SAVED) {
                                defaultValue = Route.ProfileFromIdArgs.SAVED_DEFAULT
                                type = Route.ProfileFromIdArgs.SAVED_TYPE
                            },
                        ),
                    ) {
                        val args = Route.ProfileFromIdArgs(it)
                        PersonProfileActivity(
                            personArg = Either.Left(args.id),
                            savedMode = args.saved,
                            navController = navController,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    // Necessary for deep links
                    composable(
                        route = Route.PROFILE_FROM_URL,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = Route.PROFILE_FROM_URL },
                        ),
                        arguments = listOf(
                            navArgument(Route.ProfileFromUrlArgs.NAME) {
                                type = Route.ProfileFromUrlArgs.NAME_TYPE
                            },
                            navArgument(Route.ProfileFromUrlArgs.INSTANCE) {
                                type = Route.ProfileFromUrlArgs.INSTANCE_TYPE
                            },
                        ),
                    ) {
                        val args = Route.ProfileFromUrlArgs(it)
                        val qualifiedName = "${args.name}@${args.instance}"
                        PersonProfileActivity(
                            personArg = Either.Right(qualifiedName),
                            savedMode = false,
                            navController = navController,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(
                        route = Route.COMMUNITY_LIST,
                        arguments = listOf(
                            navArgument(Route.CommunityListArgs.SELECT) {
                                defaultValue = Route.CommunityListArgs.SELECT_DEFAULT
                                type = Route.CommunityListArgs.SELECT_TYPE
                            },
                        ),
                    ) {
                        val args = Route.CommunityListArgs(it)
                        CommunityListActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            selectMode = args.select,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(
                        route = Route.CREATE_POST,
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
                            initialUrl = url,
                            initialBody = body,
                            initialImage = image,
                        )
                        activity?.intent?.replaceExtras(Bundle())
                    }

                    composable(
                        route = Route.INBOX,
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/inbox" }
                        },
                    ) {
                        InboxActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(
                        route = Route.POST,
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/post/{${Route.PostArgs.ID}}" }
                        },
                        arguments = listOf(
                            navArgument(Route.PostArgs.ID) {
                                type = Route.PostArgs.ID_TYPE
                            },
                        ),
                    ) {
                        val args = Route.PostArgs(it)
                        SwipeToNavigateBack(navController = navController) {
                            PostActivity(
                                id = Either.Left(args.id),
                                accountViewModel = accountViewModel,
                                navController = navController,
                                showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
                                showActionBarByDefault = appSettings.showCommentActionBarByDefault,
                                showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                showParentCommentNavigationButtons = appSettings.showParentCommentNavigationButtons,
                                navigateParentCommentsWithVolumeButtons = appSettings.navigateParentCommentsWithVolumeButtons,
                                siteViewModel = siteViewModel,
                                useCustomTabs = appSettings.useCustomTabs,
                                usePrivateTabs = appSettings.usePrivateTabs,
                                blurNSFW = appSettings.blurNSFW,
                            )
                        }
                    }

                    composable(
                        route = Route.COMMENT,
                        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
                            navDeepLink { uriPattern = "$instance/comment/{${Route.CommentArgs.ID}}" }
                        },
                        arguments = listOf(
                            navArgument(Route.CommentArgs.ID) {
                                type = Route.CommentArgs.ID_TYPE
                            },
                        ),
                    ) {
                        val args = Route.CommentArgs(it)
                        PostActivity(
                            id = Either.Right(args.id),
                            accountViewModel = accountViewModel,
                            navController = navController,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
                            showActionBarByDefault = appSettings.showCommentActionBarByDefault,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            showParentCommentNavigationButtons = appSettings.showParentCommentNavigationButtons,
                            navigateParentCommentsWithVolumeButtons = appSettings.navigateParentCommentsWithVolumeButtons,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(
                        route = Route.COMMENT_REPLY,
                        arguments = listOf(
                            navArgument(Route.CommentReplyArgs.IS_MODERATOR) {
                                type = Route.CommentReplyArgs.IS_MODERATOR_TYPE
                            },
                        ),
                    ) {
                        val args = Route.CommentReplyArgs(it)
                        val replyItem by navController.takeDepsFromRoot<ReplyItem>()

                        CommentReplyActivity(
                            replyItem = replyItem,
                            accountViewModel = accountViewModel,
                            navController = navController,
                            siteViewModel = siteViewModel,
                            isModerator = args.isModerator,
                        )
                    }

                    composable(route = Route.SITE_SIDEBAR) {
                        SiteSidebarActivity(
                            siteViewModel = siteViewModel,
                            navController = navController,
                        )
                    }

                    composable(route = Route.COMMENT_EDIT) {
                        val commentView by navController.takeDepsFromRoot<CommentEditDeps>()
                        CommentEditActivity(
                            commentView = commentView,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }

                    composable(route = Route.POST_EDIT) {
                        val postView by navController.takeDepsFromRoot<PostEditDeps>()
                        PostEditActivity(
                            postView = postView,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }

                    composable(route = Route.PRIVATE_MESSAGE_REPLY) {
                        val privateMessage by navController.takeDepsFromRoot<PrivateMessageDeps>()
                        PrivateMessageReplyActivity(
                            privateMessageView = privateMessage,
                            accountViewModel = accountViewModel,
                            navController = navController,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(
                        route = Route.COMMENT_REPORT,
                        arguments = listOf(
                            navArgument(Route.CommentReportArgs.ID) {
                                type = Route.CommentReportArgs.ID_TYPE
                            },
                        ),
                    ) {
                        val args = Route.CommentReportArgs(it)
                        CreateCommentReportActivity(
                            commentId = args.id,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }

                    composable(
                        route = Route.POST_REPORT,
                        arguments = listOf(
                            navArgument(Route.PostReportArgs.ID) {
                                type = Route.PostReportArgs.ID_TYPE
                            },
                        ),
                    ) {
                        val args = Route.PostReportArgs(it)
                        CreatePostReportActivity(
                            postId = args.id,
                            accountViewModel = accountViewModel,
                            navController = navController,
                        )
                    }

                    composable(route = Route.SETTINGS) {
                        SettingsActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(route = Route.LOOK_AND_FEEL) {
                        LookAndFeelActivity(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }

                    composable(
                        route = Route.ACCOUNT_SETTINGS,
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

                    composable(route = Route.ABOUT) {
                        AboutActivity(
                            navController = navController,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                        )
                    }
                }
            }
        }
    }
}
