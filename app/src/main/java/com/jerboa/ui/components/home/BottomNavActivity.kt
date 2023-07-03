package com.jerboa.ui.components.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import arrow.core.Either
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jerboa.api.ApiState
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.fetchHomePosts
import com.jerboa.loginFirstToast
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.person.PersonProfileActivity

enum class BottomNavTab {
    Home, Search, Inbox, Saved, Profile;

    fun needsLogin() = this == Inbox || this == Saved || this == Profile
}

@OptIn(
    ExperimentalAnimationApi::class,
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
) {
    val account = getCurrentAccount(accountViewModel)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val bottomNavController = rememberAnimatedNavController()
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.Home) }
    val onSelectTab = { tab: BottomNavTab ->
        if (tab.needsLogin() && account == null) {
            loginFirstToast(ctx)
        } else {
            selectedTab = tab
            bottomNavController.navigate(tab.name) {
                launchSingleTop = true
                popUpTo(0) // To make back button close the app.
            }
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val homeViewModel: HomeViewModel = viewModel()
    if (siteViewModel.siteRes is ApiState.Success) {
        InitializeRoute(homeViewModel) {
            homeViewModel.updateSortType(siteViewModel.sortType)
            homeViewModel.updateListingType(siteViewModel.listingType)
            fetchHomePosts(account, homeViewModel)
        }
    }

    ModalNavigationDrawer(
        gesturesEnabled = selectedTab == BottomNavTab.Home,
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
                        onSelectTab = if (appSettings.showBottomNav) onSelectTab else null,
                        blurNSFW = appSettings.blurNSFW,
                    )
                },
            )
        },
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        content = {
            Scaffold(
                bottomBar = {
                    BottomAppBarAll(
                        showBottomNav = appSettings.showBottomNav,
                        selectedTab = selectedTab,
                        unreadCounts = siteViewModel.getUnreadCountTotal(),
                        onSelect = onSelectTab,
                    )
                },
            ) { padding ->
                val bottomPadding =
                    if (selectedTab == BottomNavTab.Search && WindowInsets.isImeVisible) {
                        0.dp
                    } else {
                        padding.calculateBottomPadding()
                    }

                AnimatedNavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavTab.Home.name,
                    modifier = Modifier.padding(bottom = bottomPadding),
                ) {
                    composable(route = BottomNavTab.Home.name) {
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

                    composable(route = BottomNavTab.Search.name) {
                        CommunityListActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            selectMode = false,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(route = BottomNavTab.Inbox.name) {
                        InboxActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(route = BottomNavTab.Saved.name) {
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
                        )
                    }

                    composable(route = BottomNavTab.Profile.name) {
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
                        )
                    }
                }
            }
        },
    )
}
