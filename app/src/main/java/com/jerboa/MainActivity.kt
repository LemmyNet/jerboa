package com.jerboa

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import arrow.core.Either
import coil.Coil
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.feat.BackConfirmation.addConfirmationDialog
import com.jerboa.feat.BackConfirmation.addConfirmationToast
import com.jerboa.feat.BackConfirmation.disposeConfirmation
import com.jerboa.feat.BackConfirmationMode
import com.jerboa.feat.ShowConfirmationDialog
import com.jerboa.model.AccountSettingsViewModel
import com.jerboa.model.AccountSettingsViewModelFactory
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AccountViewModelFactory
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.AppSettingsViewModelFactory
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.ban.BanFromCommunityActivity
import com.jerboa.ui.components.ban.BanPersonActivity
import com.jerboa.ui.components.comment.edit.CommentEditActivity
import com.jerboa.ui.components.comment.reply.CommentReplyActivity
import com.jerboa.ui.components.common.LinkDropDownMenu
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.common.ShowChangelog
import com.jerboa.ui.components.common.SwipeToNavigateBack
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.sidebar.CommunitySidebarActivity
import com.jerboa.ui.components.home.BottomNavActivity
import com.jerboa.ui.components.home.sidebar.SiteSidebarActivity
import com.jerboa.ui.components.imageviewer.ImageViewer
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.create.CreatePostActivity
import com.jerboa.ui.components.post.edit.PostEditActivity
import com.jerboa.ui.components.privatemessage.CreatePrivateMessageActivity
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyActivity
import com.jerboa.ui.components.remove.comment.CommentRemoveActivity
import com.jerboa.ui.components.remove.post.PostRemoveActivity
import com.jerboa.ui.components.report.comment.CreateCommentReportActivity
import com.jerboa.ui.components.report.post.CreatePostReportActivity
import com.jerboa.ui.components.settings.SettingsActivity
import com.jerboa.ui.components.settings.about.AboutActivity
import com.jerboa.ui.components.settings.account.AccountSettingsActivity
import com.jerboa.ui.components.settings.crashlogs.CrashLogsActivity
import com.jerboa.ui.components.settings.lookandfeel.LookAndFeelActivity
import com.jerboa.ui.theme.JerboaTheme
import com.jerboa.util.markwon.BetterLinkMovementMethod

class MainActivity : AppCompatActivity() {
    val siteViewModel by viewModels<SiteViewModel>(factoryProducer = { SiteViewModel.Factory })
    val accountViewModel by viewModels<AccountViewModel>(factoryProducer = { AccountViewModelFactory.Factory })
    private val appSettingsViewModel by viewModels<AppSettingsViewModel>(factoryProducer = { AppSettingsViewModelFactory.Factory })
    private val accountSettingsViewModel by viewModels<AccountSettingsViewModel>(
        factoryProducer = { AccountSettingsViewModelFactory.Factory },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val ctx = LocalContext.current

            val appSettings by appSettingsViewModel.appSettings.observeAsState(APP_SETTINGS_DEFAULT)

            @Suppress("SENSELESS_COMPARISON")
            if (appSettings == null) {
                triggerRebirth(ctx)
            }

            if (appSettings.autoPlayGifs) {
                Coil.setImageLoader((ctx.applicationContext as JerboaApplication).imageGifLoader)
            } else {
                Coil.setImageLoader(ctx.applicationContext as JerboaApplication)
            }

            JerboaTheme(
                appSettings = appSettings,
            ) {
                val appState = rememberJerboaAppState()

                val showConfirmationDialog = remember { mutableStateOf(false) }

                if (showConfirmationDialog.value) {
                    ShowConfirmationDialog({ showConfirmationDialog.value = false }, ::finish)
                }

                DisposableEffect(appSettings.backConfirmationMode) {
                    when (BackConfirmationMode.entries[appSettings.backConfirmationMode]) {
                        BackConfirmationMode.Toast -> {
                            this@MainActivity.addConfirmationToast(appState.navController, ctx)
                        }
                        BackConfirmationMode.Dialog -> {
                            this@MainActivity.addConfirmationDialog(appState.navController) { showConfirmationDialog.value = true }
                        }
                        BackConfirmationMode.None -> {}
                    }

                    onDispose {
                        disposeConfirmation()
                    }
                }

                MarkdownHelper.init(
                    appState,
                    appSettings.useCustomTabs,
                    appSettings.usePrivateTabs,
                    object : BetterLinkMovementMethod.OnLinkLongClickListener {
                        override fun onLongClick(
                            textView: TextView,
                            url: String,
                        ): Boolean {
                            appState.showLinkPopup(url)
                            return true
                        }
                    },
                )

                LinkDropDownMenu(
                    appState.linkDropdownExpanded.value,
                    appState::hideLinkPopup,
                    appState,
                    appSettings.useCustomTabs,
                    appSettings.usePrivateTabs,
                )

                ShowChangelog(appSettingsViewModel = appSettingsViewModel)

                val drawerState = rememberDrawerState(DrawerValue.Closed)

                NavHost(
                    route = Route.Graph.ROOT,
                    navController = appState.navController,
                    startDestination = Route.HOME,
                    enterTransition = {
                        slideInHorizontally { it }
                    },
                    exitTransition =
                        {
                            // No animation for image viewer
                            if (this.targetState.destination.route == Route.VIEW) {
                                ExitTransition.None
                            } else {
                                slideOutHorizontally { -it }
                            }
                        },
                    popEnterTransition = {
                        // No animation for image viewer
                        if (this.initialState.destination.route == Route.VIEW) {
                            EnterTransition.None
                        } else {
                            slideInHorizontally { -it }
                        }
                    },
                    popExitTransition = {
                        slideOutHorizontally {
                            it
                        }
                    },
                ) {
                    composable(
                        route = Route.LOGIN,
                        deepLinks =
                            DEFAULT_LEMMY_INSTANCES.map { instance ->
                                navDeepLink { uriPattern = "$instance/login" }
                            },
                    ) {
                        LoginActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.HOME) {
                        BottomNavActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            appSettings = appSettings,
                            drawerState = drawerState,
                        )
                    }

                    composable(
                        route = Route.COMMUNITY_FROM_ID,
                        arguments =
                            listOf(
                                navArgument(Route.CommunityFromIdArgs.ID) {
                                    type = Route.CommunityFromIdArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommunityFromIdArgs(it)

                        CommunityActivity(
                            communityArg = Either.Left(args.id),
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    // Only necessary for community deeplinks
                    composable(
                        route = Route.COMMUNITY_FROM_URL,
                        deepLinks =
                            listOf(
                                navDeepLink { uriPattern = Route.COMMUNITY_FROM_URL },
                            ),
                        arguments =
                            listOf(
                                navArgument(Route.CommunityFromUrlArgs.NAME) {
                                    type = Route.CommunityFromUrlArgs.NAME_TYPE
                                },
                                navArgument(Route.CommunityFromUrlArgs.INSTANCE) {
                                    type = Route.CommunityFromUrlArgs.INSTANCE_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommunityFromUrlArgs(it)
                        // Could be instance/c/community@otherinstance ({instance}/c/{name})
                        // Name could contain its instance already, thus we check for it
                        val qualifiedName =
                            if (args.name.contains("@")) {
                                args.name
                            } else {
                                "${args.name}@${args.instance}"
                            }

                        CommunityActivity(
                            communityArg = Either.Right(qualifiedName),
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    composable(route = Route.COMMUNITY_SIDEBAR) {
                        CommunitySidebarActivity(
                            appState = appState,
                            onClickBack = appState::popBackStack,
                        )
                    }

                    composable(
                        route = Route.PROFILE_FROM_ID,
                        arguments =
                            listOf(
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
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            drawerState = drawerState,
                            onBack = appState::popBackStack,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    // Necessary for deep links
                    composable(
                        route = Route.PROFILE_FROM_URL,
                        deepLinks =
                            listOf(
                                navDeepLink { uriPattern = Route.PROFILE_FROM_URL },
                            ),
                        arguments =
                            listOf(
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
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            drawerState = drawerState,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    composable(
                        route = Route.COMMUNITY_LIST,
                        arguments =
                            listOf(
                                navArgument(Route.CommunityListArgs.SELECT) {
                                    defaultValue = Route.CommunityListArgs.SELECT_DEFAULT
                                    type = Route.CommunityListArgs.SELECT_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommunityListArgs(it)
                        CommunityListActivity(
                            appState = appState,
                            selectMode = args.select,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                            followList = siteViewModel.getFollowList(),
                        )
                    }

                    composable(
                        route = Route.CREATE_POST,
                        deepLinks =
                            listOf(
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
                            url = text.padUrlWithHttps()
                        } else {
                            body = text
                        }
                        CreatePostActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            initialUrl = url,
                            initialBody = body,
                            initialImage = image,
                        )
                        activity?.intent?.replaceExtras(Bundle())
                    }

                    composable(
                        route = Route.INBOX,
                        deepLinks =
                            DEFAULT_LEMMY_INSTANCES.map { instance ->
                                navDeepLink { uriPattern = "$instance/inbox" }
                            },
                    ) {
                        InboxActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                        )
                    }

                    composable(
                        route = Route.POST,
                        deepLinks =
                            DEFAULT_LEMMY_INSTANCES.map { instance ->
                                navDeepLink { uriPattern = "$instance/post/{${Route.PostArgs.ID}}" }
                            },
                        arguments =
                            listOf(
                                navArgument(Route.PostArgs.ID) {
                                    type = Route.PostArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.PostArgs(it)

                        SwipeToNavigateBack(
                            appSettings.postNavigationGestureMode,
                            appState::navigateUp,
                        ) {
                            PostActivity(
                                id = Either.Left(args.id),
                                accountViewModel = accountViewModel,
                                appState = appState,
                                showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
                                showActionBarByDefault = appSettings.showCommentActionBarByDefault,
                                showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                showParentCommentNavigationButtons = appSettings.showParentCommentNavigationButtons,
                                navigateParentCommentsWithVolumeButtons = appSettings.navigateParentCommentsWithVolumeButtons,
                                siteViewModel = siteViewModel,
                                useCustomTabs = appSettings.useCustomTabs,
                                usePrivateTabs = appSettings.usePrivateTabs,
                                blurNSFW = appSettings.blurNSFW,
                                showPostLinkPreview = appSettings.showPostLinkPreviews,
                                postActionbarMode = appSettings.postActionbarMode,
                                swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            )
                        }
                    }

                    composable(
                        route = Route.COMMENT,
                        deepLinks =
                            DEFAULT_LEMMY_INSTANCES.map { instance ->
                                navDeepLink { uriPattern = "$instance/comment/{${Route.CommentArgs.ID}}" }
                            },
                        arguments =
                            listOf(
                                navArgument(Route.CommentArgs.ID) {
                                    type = Route.CommentArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommentArgs(it)
                        PostActivity(
                            id = Either.Right(args.id),
                            accountViewModel = accountViewModel,
                            appState = appState,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
                            showActionBarByDefault = appSettings.showCommentActionBarByDefault,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            showParentCommentNavigationButtons = appSettings.showParentCommentNavigationButtons,
                            navigateParentCommentsWithVolumeButtons = appSettings.navigateParentCommentsWithVolumeButtons,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreview = appSettings.showPostLinkPreviews,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    composable(
                        route = Route.COMMENT_REPLY,
                    ) {
                        CommentReplyActivity(
                            accountViewModel = accountViewModel,
                            appState = appState,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.SITE_SIDEBAR) {
                        SiteSidebarActivity(
                            siteViewModel = siteViewModel,
                            onBackClick = appState::popBackStack,
                        )
                    }

                    composable(route = Route.COMMENT_EDIT) {
                        CommentEditActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(route = Route.POST_EDIT) {
                        PostEditActivity(
                            accountViewModel = accountViewModel,
                            appState = appState,
                        )
                    }

                    composable(route = Route.PRIVATE_MESSAGE_REPLY) {
                        PrivateMessageReplyActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            onBack = appState::popBackStack,
                            onProfile = appState::toProfile,
                        )
                    }

                    composable(
                        route = Route.POST_REMOVE,
                    ) {
                        PostRemoveActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.POST_REMOVE,
                    ) {
                        PostRemoveActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.COMMENT_REMOVE,
                    ) {
                        CommentRemoveActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.BAN_PERSON,
                    ) {
                        BanPersonActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.BAN_FROM_COMMUNITY,
                    ) {
                        BanFromCommunityActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.POST_REPORT,
                        arguments =
                            listOf(
                                navArgument(Route.PostReportArgs.ID) {
                                    type = Route.PostReportArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.PostReportArgs(it)
                        CreatePostReportActivity(
                            postId = args.id,
                            accountViewModel = accountViewModel,
                            onBack = appState::navigateUp,
                        )
                    }

                    composable(
                        route = Route.COMMENT_REPORT,
                        arguments =
                            listOf(
                                navArgument(Route.CommentReportArgs.ID) {
                                    type = Route.CommentReportArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommentReportArgs(it)
                        CreateCommentReportActivity(
                            commentId = args.id,
                            accountViewModel = accountViewModel,
                            onBack = appState::navigateUp,
                        )
                    }

                    composable(route = Route.SETTINGS) {
                        SettingsActivity(
                            accountViewModel = accountViewModel,
                            onBack = appState::popBackStack,
                            onClickAbout = appState::toAbout,
                            onClickAccountSettings = appState::toAccountSettings,
                            onClickLookAndFeel = appState::toLookAndFeel,
                        )
                    }

                    composable(route = Route.LOOK_AND_FEEL) {
                        LookAndFeelActivity(
                            appSettingsViewModel = appSettingsViewModel,
                            onBack = appState::popBackStack,
                        )
                    }

                    composable(
                        route = Route.ACCOUNT_SETTINGS,
                        deepLinks =
                            DEFAULT_LEMMY_INSTANCES.map { instance ->
                                navDeepLink { uriPattern = "$instance/settings" }
                            },
                    ) {
                        AccountSettingsActivity(
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            accountSettingsViewModel = accountSettingsViewModel,
                            onBack = appState::popBackStack,
                        )
                    }

                    composable(route = Route.ABOUT) {
                        AboutActivity(
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            onBack = appState::popBackStack,
                            onClickCrashLogs = appState::toCrashLogs,
                            openLinkRaw = appState::openLinkRaw,
                        )
                    }

                    composable(route = Route.CRASH_LOGS) {
                        CrashLogsActivity(
                            onClickBack = appState::popBackStack,
                        )
                    }

                    composable(
                        route = Route.VIEW,
                        arguments =
                            listOf(
                                navArgument(Route.ViewArgs.URL) {
                                    type = Route.ViewArgs.URL_TYPE
                                },
                            ),
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                    ) {
                        val args = Route.ViewArgs(it)

                        ImageViewer(url = args.url, appState = appState)
                    }

                    composable(
                        route = Route.CREATE_PRIVATE_MESSAGE,
                        arguments =
                            listOf(
                                navArgument(Route.CreatePrivateMessageArgs.PERSON_ID) {
                                    type = Route.CreatePrivateMessageArgs.PERSON_ID_TYPE
                                },
                                navArgument(Route.CreatePrivateMessageArgs.PERSON_NAME) {
                                    type = Route.CreatePrivateMessageArgs.PERSON_NAME_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CreatePrivateMessageArgs(it)

                        CreatePrivateMessageActivity(
                            args.personId,
                            args.personName,
                            accountViewModel,
                            appState::popBackStack,
                        )
                    }
                }
            }
        }
    }
}
