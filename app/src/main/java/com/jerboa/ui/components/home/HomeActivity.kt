package com.jerboa.ui.components.home

import android.app.Activity
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jerboa.R
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.nav.bottomNavDefaultRoute
import com.jerboa.nav.bottomNavIsHome
import com.jerboa.nav.bottomNavIsInbox
import com.jerboa.nav.bottomNavIsProfile
import com.jerboa.nav.bottomNavIsSaved
import com.jerboa.nav.bottomNavIsSearch
import com.jerboa.nav.bottomNavSelectHome
import com.jerboa.nav.bottomNavSelectInbox
import com.jerboa.nav.bottomNavSelectProfile
import com.jerboa.nav.bottomNavSelectSaved
import com.jerboa.nav.bottomNavSelectSearch
import com.jerboa.nav.communityListScreen
import com.jerboa.nav.feedScreen
import com.jerboa.nav.inboxScreen
import com.jerboa.nav.noEnterTransition
import com.jerboa.nav.noPopExitTransition
import com.jerboa.nav.profileScreen
import com.jerboa.nav.showLogin
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.common.InboxIconAndBadge
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel
import com.jerboa.unreadCountTotal

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    communityListViewModel: CommunityListViewModel,
    inboxViewModel: InboxViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    personProfileViewModel: PersonProfileViewModel,
    commentEditViewModel: CommentEditViewModel,
    appSettings: AppSettings?,
) {
    Log.d("jerboa", "got to home activity")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    var selectedBottomNavBarType by rememberSaveable { mutableStateOf(BottomNavBarType.Home) }
    val bottomNavController = rememberAnimatedNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        selectedBottomNavBarType = when {
            navBackStackEntry?.bottomNavIsHome() ?: false -> BottomNavBarType.Home
            navBackStackEntry?.bottomNavIsSearch() ?: false -> BottomNavBarType.Search
            navBackStackEntry?.bottomNavIsInbox() ?: false -> BottomNavBarType.Inbox
            navBackStackEntry?.bottomNavIsSaved() ?: false -> BottomNavBarType.Saved
            navBackStackEntry?.bottomNavIsProfile() ?: false -> BottomNavBarType.Profile
            else -> BottomNavBarType.Home
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    MainDrawer(
                        siteViewModel = siteViewModel,
                        navController = navController,
                        bottomNavController = bottomNavController,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        ctx = ctx,
                    )
                },
            )
        },
        content = {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = { padding ->
                    AnimatedNavHost(
                        navController = bottomNavController,
                        startDestination = bottomNavDefaultRoute(),
                        modifier = Modifier.padding(),
                        enterTransition = { noEnterTransition },
                        popExitTransition = { noPopExitTransition },
                    ) {
                        feedScreen(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            drawerState = drawerState,
                            appSettings = appSettings,
                        )

                        communityListScreen(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            communityListViewModel = communityListViewModel,
                            siteViewModel = siteViewModel,
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
                    }
                },
                bottomBar = {
                    BottomNavBar(
                        showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                        selectedType = selectedBottomNavBarType,
                        unreadCounts = homeViewModel.unreadCountResponse,
                        onClickHome = {
                            selectedBottomNavBarType = BottomNavBarType.Home
                            bottomNavController.bottomNavSelectHome()
                        },
                        onClickSearch = {
                            selectedBottomNavBarType = BottomNavBarType.Search
                            bottomNavController.bottomNavSelectSearch()
                        },
                        onClickInbox = {
                            account?.also {
                                selectedBottomNavBarType = BottomNavBarType.Inbox
                                bottomNavController.bottomNavSelectInbox()
                            } ?: run {
                                navController.showLogin()
                            }
                        },
                        onClickSaved = {
                            account?.id?.also {
                                selectedBottomNavBarType = BottomNavBarType.Saved
                                bottomNavController.bottomNavSelectSaved(it)
                            } ?: run {
                                navController.showLogin()
                            }
                        },
                        onClickProfile = {
                            account?.id?.also {
                                selectedBottomNavBarType = BottomNavBarType.Profile
                                bottomNavController.bottomNavSelectProfile(it)
                            } ?: run {
                                navController.showLogin()
                            }
                        },
                    )
                },
            )
        },
    )
}

enum class BottomNavBarType {
    Home, Search, Inbox, Saved, Profile,
}

@Composable
fun BottomNavBar(
    selectedType: BottomNavBarType,
    unreadCounts: GetUnreadCountResponse? = null,
    showBottomNav: Boolean? = true,
    onClickHome: () -> Unit,
    onClickSearch: () -> Unit,
    onClickSaved: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
) {
    val totalUnreads = unreadCounts?.let { unreadCountTotal(it) }

    if (showBottomNav == true) {
        // Check for preview mode
        if (LocalContext.current is Activity) {
            val window = (LocalContext.current as Activity).window
            val colorScheme = MaterialTheme.colorScheme

            DisposableEffect(Unit) {
                window.navigationBarColor = colorScheme.surfaceColorAtElevation(3.dp).toArgb()

                onDispose {
                    window.navigationBarColor = colorScheme.background.toArgb()
                }
            }
        }

        NavigationBar {
            for (type in BottomNavBarType.values()) {
                val selected = type == selectedType
                NavigationBarItem(
                    icon = {
                        InboxIconAndBadge(
                            iconBadgeCount = if (type == BottomNavBarType.Inbox) totalUnreads else null,
                            icon = if (selected) {
                                when (type) {
                                    BottomNavBarType.Home -> Icons.Filled.Home
                                    BottomNavBarType.Search -> Icons.Filled.Search
                                    BottomNavBarType.Inbox -> Icons.Filled.Email
                                    BottomNavBarType.Saved -> Icons.Filled.Bookmarks
                                    BottomNavBarType.Profile -> Icons.Filled.Person
                                }
                            } else {
                                when (type) {
                                    BottomNavBarType.Home -> Icons.Outlined.Home
                                    BottomNavBarType.Search -> Icons.Outlined.Search
                                    BottomNavBarType.Inbox -> Icons.Outlined.Email
                                    BottomNavBarType.Saved -> Icons.Outlined.Bookmarks
                                    BottomNavBarType.Profile -> Icons.Outlined.Person
                                }
                            },
                            contentDescription = stringResource(
                                when (type) {
                                    BottomNavBarType.Home -> R.string.bottomBar_home
                                    BottomNavBarType.Search -> R.string.bottomBar_search
                                    BottomNavBarType.Inbox -> R.string.bottomBar_inbox
                                    BottomNavBarType.Saved -> R.string.bottomBar_bookmarks
                                    BottomNavBarType.Profile -> R.string.bottomBar_profile
                                },
                            ),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(
                                when (type) {
                                    BottomNavBarType.Home -> R.string.bottomBar_label_home
                                    BottomNavBarType.Search -> R.string.bottomBar_label_search
                                    BottomNavBarType.Inbox -> R.string.bottomBar_label_inbox
                                    BottomNavBarType.Saved -> R.string.bottomBar_label_bookmarks
                                    BottomNavBarType.Profile -> R.string.bottomBar_label_profile
                                },
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    selected = selected,
                    onClick = when (type) {
                        BottomNavBarType.Home -> onClickHome
                        BottomNavBarType.Search -> onClickSearch
                        BottomNavBarType.Inbox -> onClickInbox
                        BottomNavBarType.Saved -> onClickSaved
                        BottomNavBarType.Profile -> onClickProfile
                    },
                )
            }
        }
    }
}
