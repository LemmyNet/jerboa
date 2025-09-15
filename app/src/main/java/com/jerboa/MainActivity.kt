package com.jerboa

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.jerboa.ui.components.ban.BanFromCommunityScreen
import com.jerboa.ui.components.ban.BanPersonScreen
import com.jerboa.ui.components.comment.edit.CommentEditScreen
import com.jerboa.ui.components.comment.reply.CommentReplyScreen
import com.jerboa.ui.components.common.LinkDropDownMenu
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.common.SwipeToNavigateBack
import com.jerboa.ui.components.community.CommunityScreen
import com.jerboa.ui.components.community.list.CommunityListScreen
import com.jerboa.ui.components.community.sidebar.CommunitySidebarScreen
import com.jerboa.ui.components.home.BottomNavScreen
import com.jerboa.ui.components.home.ShowAppStartupDialogs
import com.jerboa.ui.components.home.legal.SiteLegalScreen
import com.jerboa.ui.components.home.sidebar.SiteSidebarScreen
import com.jerboa.ui.components.imageviewer.ImageViewerScreen
import com.jerboa.ui.components.inbox.InboxScreen
import com.jerboa.ui.components.login.LoginScreen
import com.jerboa.ui.components.person.PersonProfileScreen
import com.jerboa.ui.components.post.PostScreen
import com.jerboa.ui.components.post.create.CreatePostScreen
import com.jerboa.ui.components.post.edit.PostEditScreen
import com.jerboa.ui.components.privatemessage.CreatePrivateMessageScreen
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyScreen
import com.jerboa.ui.components.registrationapplications.RegistrationApplicationsScreen
import com.jerboa.ui.components.remove.comment.CommentRemoveScreen
import com.jerboa.ui.components.remove.post.PostRemoveScreen
import com.jerboa.ui.components.report.comment.CreateCommentReportScreen
import com.jerboa.ui.components.report.post.CreatePostReportScreen
import com.jerboa.ui.components.reports.ReportsScreen
import com.jerboa.ui.components.settings.SettingsScreen
import com.jerboa.ui.components.settings.about.AboutScreen
import com.jerboa.ui.components.settings.account.AccountSettingsScreen
import com.jerboa.ui.components.settings.backupandrestore.BackupAndRestoreScreen
import com.jerboa.ui.components.settings.block.BlocksScreen
import com.jerboa.ui.components.settings.crashlogs.CrashLogsScreen
import com.jerboa.ui.components.settings.lookandfeel.LookAndFeelScreen
import com.jerboa.ui.components.videoviewer.VideoViewerScreen
import com.jerboa.ui.components.viewvotes.comment.CommentLikesScreen
import com.jerboa.ui.components.viewvotes.post.PostLikesScreen
import com.jerboa.ui.theme.JerboaTheme
import com.jerboa.util.markwon.BetterLinkMovementMethod

class MainActivity : AppCompatActivity() {
    val siteViewModel by viewModels<SiteViewModel>(factoryProducer = { SiteViewModel.Factory })
    val accountViewModel by viewModels<AccountViewModel>(factoryProducer = { AccountViewModelFactory.Factory })
    private val appSettingsViewModel by viewModels<AppSettingsViewModel>(factoryProducer = { AppSettingsViewModelFactory.Factory })
    private val accountSettingsViewModel by viewModels<AccountSettingsViewModel>(
        factoryProducer = { AccountSettingsViewModelFactory.Factory },
    )

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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

                DisposableEffect(Unit) {
                    onDispose {
                        appState.release()
                    }
                }

                val showConfirmationDialog = remember { mutableStateOf(false) }

                if (showConfirmationDialog.value) {
                    ShowConfirmationDialog({ showConfirmationDialog.value = false }, ::finish)
                }

                DisposableEffect(appSettings.backConfirmationMode) {
                    when (appSettings.backConfirmationMode.toEnum<BackConfirmationMode>()) {
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

                ShowAppStartupDialogs(
                    appSettingsViewModel = appSettingsViewModel,
                    siteViewModel = siteViewModel,
                )

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
                            if (this.targetState.destination.route == Route.IMAGE_VIEW) {
                                ExitTransition.None
                            } else {
                                slideOutHorizontally { -it }
                            }
                        },
                    popEnterTransition = {
                        // No animation for image viewer
                        if (this.initialState.destination.route == Route.IMAGE_VIEW) {
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
                        LoginScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.HOME) {
                        BottomNavScreen(
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

                        CommunityScreen(
                            communityArg = Either.Left(args.id),
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
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

                        CommunityScreen(
                            communityArg = Either.Right(qualifiedName),
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
                        )
                    }

                    composable(route = Route.COMMUNITY_SIDEBAR) {
                        CommunitySidebarScreen(
                            appState = appState,
                            onClickBack = appState::popBackStack,
                            showAvatar = siteViewModel.showAvatar(),
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
                        PersonProfileScreen(
                            personArg = Either.Left(args.id),
                            savedMode = args.saved,
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            drawerState = drawerState,
                            onBack = appState::popBackStack,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
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
                        PersonProfileScreen(
                            personArg = Either.Right(qualifiedName),
                            savedMode = false,
                            appState = appState,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            drawerState = drawerState,
                            onBack = appState::popBackStack,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
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
                        CommunityListScreen(
                            appState = appState,
                            selectMode = args.select,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            drawerState = drawerState,
                            followList = siteViewModel.getFollowList(),
                            showAvatar = siteViewModel.showAvatar(),
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
                        CreatePostScreen(
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
                        InboxScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            drawerState = drawerState,
                        )
                    }

                    composable(
                        route = Route.REGISTRATION_APPLICATIONS,
                    ) {
                        RegistrationApplicationsScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            drawerState = drawerState,
                        )
                    }

                    composable(
                        route = Route.REGISTRATION_APPLICATIONS,
                    ) {
                        RegistrationApplicationsScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            drawerState = drawerState,
                        )
                    }

                    composable(
                        route = Route.REPORTS,
                    ) {
                        ReportsScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            drawerState = drawerState,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
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
                            appSettings.postNavigationGestureMode.toEnumSafe(),
                            appState::navigateUp,
                        ) {
                            PostScreen(
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
                                blurNSFW = appSettings.blurNSFW.toEnum(),
                                showPostLinkPreview = appSettings.showPostLinkPreviews,
                                postActionBarMode = appSettings.postActionBarMode.toEnum(),
                                swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                                disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
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
                        PostScreen(
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
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            showPostLinkPreview = appSettings.showPostLinkPreviews,
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
                        )
                    }

                    composable(
                        route = Route.COMMENT_REPLY,
                    ) {
                        CommentReplyScreen(
                            accountViewModel = accountViewModel,
                            appState = appState,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.SITE_SIDEBAR) {
                        SiteSidebarScreen(
                            appState = appState,
                            siteViewModel = siteViewModel,
                        )
                    }

                    composable(route = Route.SITE_LEGAL) {
                        SiteLegalScreen(
                            siteViewModel = siteViewModel,
                            onBackClick = appState::popBackStack,
                        )
                    }

                    composable(route = Route.COMMENT_EDIT) {
                        CommentEditScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(route = Route.POST_EDIT) {
                        PostEditScreen(
                            accountViewModel = accountViewModel,
                            appState = appState,
                        )
                    }

                    composable(route = Route.PRIVATE_MESSAGE_REPLY) {
                        PrivateMessageReplyScreen(
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
                        PostRemoveScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.POST_REMOVE,
                    ) {
                        PostRemoveScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.COMMENT_REMOVE,
                    ) {
                        CommentRemoveScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.BAN_PERSON,
                    ) {
                        BanPersonScreen(
                            appState = appState,
                            accountViewModel = accountViewModel,
                        )
                    }

                    composable(
                        route = Route.BAN_FROM_COMMUNITY,
                    ) {
                        BanFromCommunityScreen(
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
                        CreatePostReportScreen(
                            postId = args.id,
                            accountViewModel = accountViewModel,
                            onBack = appState::navigateUp,
                        )
                    }

                    composable(
                        route = Route.POST_LIKES,
                        arguments =
                            listOf(
                                navArgument(Route.PostLikesArgs.ID) {
                                    type = Route.PostLikesArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.PostLikesArgs(it)
                        PostLikesScreen(
                            appState = appState,
                            postId = args.id,
                            onBack = appState::navigateUp,
                        )
                    }

                    composable(
                        route = Route.COMMENT_LIKES,
                        arguments =
                            listOf(
                                navArgument(Route.CommentLikesArgs.ID) {
                                    type = Route.CommentLikesArgs.ID_TYPE
                                },
                            ),
                    ) {
                        val args = Route.CommentLikesArgs(it)
                        CommentLikesScreen(
                            appState = appState,
                            commentId = args.id,
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
                        CreateCommentReportScreen(
                            commentId = args.id,
                            accountViewModel = accountViewModel,
                            onBack = appState::navigateUp,
                        )
                    }

                    composable(route = Route.SETTINGS) {
                        SettingsScreen(
                            accountViewModel = accountViewModel,
                            onBack = appState::popBackStack,
                            onClickAbout = appState::toAbout,
                            onClickAccountSettings = appState::toAccountSettings,
                            onClickBlocks = appState::toBlockView,
                            onClickLookAndFeel = appState::toLookAndFeel,
                            onClickBackupAndRestore = appState::toBackupAndRestore,
                        )
                    }

                    composable(route = Route.LOOK_AND_FEEL) {
                        LookAndFeelScreen(
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
                        AccountSettingsScreen(
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            accountSettingsViewModel = accountSettingsViewModel,
                            onBack = appState::popBackStack,
                        )
                    }

                    composable(route = Route.BACKUP_AND_RESTORE) {
                        BackupAndRestoreScreen(
                            onBack = appState::popBackStack,
                        )
                    }

                    composable(route = Route.ABOUT) {
                        AboutScreen(
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            onBack = appState::popBackStack,
                            onClickCrashLogs = appState::toCrashLogs,
                            openLinkRaw = appState::openLinkRaw,
                        )
                    }

                    composable(route = Route.BLOCK_VIEW) {
                        BlocksScreen(
                            siteViewModel = siteViewModel,
                            onBack = appState::popBackStack,
                        )
                    }

                    composable(route = Route.CRASH_LOGS) {
                        CrashLogsScreen(
                            onClickBack = appState::popBackStack,
                        )
                    }

                    composable(
                        route = Route.IMAGE_VIEW,
                        arguments =
                            listOf(
                                navArgument(Route.ImageViewArgs.URL) {
                                    type = Route.ImageViewArgs.URL_TYPE
                                },
                            ),
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                    ) {
                        val args = Route.ImageViewArgs(it)
                        ImageViewerScreen(url = args.url, appState = appState)
                    }

                    composable(
                        route = Route.VIDEO_VIEW,
                        arguments =
                            listOf(
                                navArgument(Route.VideoViewArgs.URL) {
                                    type = Route.VideoViewArgs.URL_TYPE
                                },
                            ),
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                    ) {
                        val args = Route.VideoViewArgs(it)
                        VideoViewerScreen(url = args.url, appState = appState)
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

                        CreatePrivateMessageScreen(
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
