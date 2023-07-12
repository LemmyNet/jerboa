package com.jerboa.ui.components.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import arrow.core.Either
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.AppSettings
import com.jerboa.fetchHomePosts
import com.jerboa.loginFirstToast
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.toView
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.drawer.MainDrawer
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.person.PersonProfileActivity

enum class NavTab(
    val textId: Int,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val contentDescriptionId: Int,
) {
    Home(R.string.bottomBar_label_home, Icons.Outlined.Home, Icons.Filled.Home, R.string.bottomBar_home),
    Search(R.string.bottomBar_label_search, Icons.Outlined.Search, Icons.Filled.Search, R.string.bottomBar_search),
    Inbox(R.string.bottomBar_label_inbox, Icons.Outlined.Email, Icons.Filled.Email, R.string.bottomBar_inbox),
    Saved(R.string.bottomBar_label_bookmarks, Icons.Outlined.Bookmarks, Icons.Filled.Bookmarks, R.string.bottomBar_bookmarks),
    Profile(R.string.bottomBar_label_profile, Icons.Outlined.Person, Icons.Filled.Person, R.string.bottomBar_profile),
    ;

    fun needsLogin() = this == Inbox || this == Saved || this == Profile
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun BottomNavActivity(
    navController: NavController,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings,
    drawerState: DrawerState,
) {
    val account = getCurrentAccount(accountViewModel)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val bottomNavController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(NavTab.Home) }
    val onSelectTab = { tab: NavTab ->
        if (tab.needsLogin() && account == null) {
            loginFirstToast(ctx)
        } else {
            selectedTab = tab
            bottomNavController.navigate(tab.name) {
                launchSingleTop = true
                popUpTo(bottomNavController.graph.id) // To make back button close the app.
            }
        }
    }

    val homeViewModel: HomeViewModel = viewModel()
    if (siteViewModel.siteRes is ApiState.Success) {
        InitializeRoute(homeViewModel) {
            homeViewModel.updateSortType(siteViewModel.sortType)
            homeViewModel.updateListingType(siteViewModel.listingType)
            fetchHomePosts(account, homeViewModel)
        }
    }

    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    MainDrawer(
                        siteViewModel = siteViewModel,
                        navController = navController,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        onSelectTab = onSelectTab,
                        blurNSFW = appSettings.blurNSFW,
                        showBottomNav = appSettings.showBottomNav,
                    )
                },
            )
        },
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        content = {
            Scaffold(
                bottomBar = {
                    if (appSettings.showBottomNav) {
                        BottomAppBarAll(
                            selectedTab = selectedTab,
                            unreadCounts = siteViewModel.getUnreadCountTotal(),
                            showTextDescriptionsInNavbar = appSettings.showTextDescriptionsInNavbar,
                            onSelect = onSelectTab,
                        )
                    }
                },
            ) { padding ->
                val bottomPadding =
                    if (selectedTab == NavTab.Search && WindowInsets.isImeVisible) {
                        0.dp
                    } else {
                        padding.calculateBottomPadding()
                    }

                NavHost(
                    navController = bottomNavController,
                    startDestination = NavTab.Home.name,
                    modifier = Modifier.padding(bottom = bottomPadding),
                ) {
                    composable(route = NavTab.Home.name) {
                        HomeActivity(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            drawerState = drawerState,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(route = NavTab.Search.name) {
                        CommunityListActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            selectMode = false,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = NavTab.Inbox.name) {
                        InboxActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = NavTab.Saved.name) {
                        PersonProfileActivity(
                            personArg = Either.Left(account!!.id),
                            savedMode = true,
                            navController = navController,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                            openImageViewer = navController::toView,
                        )
                    }

                    composable(route = NavTab.Profile.name) {
                        PersonProfileActivity(
                            personArg = Either.Left(account!!.id),
                            savedMode = false,
                            navController = navController,
                            accountViewModel = accountViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW,
                            openImageViewer = navController::toView,
                            drawerState = drawerState,
                        )
                    }
                }
            }
        },
    )
}
