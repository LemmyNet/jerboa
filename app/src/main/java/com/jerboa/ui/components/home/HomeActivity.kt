package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import arrow.core.Either
import com.jerboa.closeDrawer
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.fetchHomePosts
import com.jerboa.fetchInitialData
import com.jerboa.nav.HomeTab
import com.jerboa.nav.Route
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.list.CommunityListNavController
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.inbox.InboxNavController
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileNavController
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActivity(
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    selectTabArg: HomeTab,
    feedNavController: FeedNavController,
    communityListNavController: CommunityListNavController,
    inboxNavController: InboxNavController,
    savedAndProfileNavController: PersonProfileNavController,
) {
    Log.d("jerboa", "got to home activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val bottomNavController = rememberNavController()

    // The bottomNavController remembers which route to show. Therefore, the selectedTab also
    // has to be persisted across navigations to ensure that the tab selected is also the route
    // being shown.
    var selectedTab by rememberSaveable { mutableStateOf(selectTabArg) }
    LaunchedEffect(account) {
        // Required for the inbox deeplink.
        if (selectedTab.needsLogin() && account == null) {
            selectedTab = Route.HomeArgs.TAB_DEFAULT
        }
    }

    val onSelectTab = { tab: HomeTab ->
        if (tab.needsLogin() && account == null) {
            feedNavController.toLogin.navigate()
        } else {
            selectedTab = tab
            bottomNavController.navigate(tab.name) {
                popUpTo(0) // To make back button close the app.
            }
        }
    }

    val homeViewModel: HomeViewModel = viewModel()
    InitializeRoute {
        fetchHomePosts(account, homeViewModel)
    }

    ModalNavigationDrawer(
        gesturesEnabled = selectedTab == HomeTab.Feed,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    MainDrawer(
                        siteViewModel = siteViewModel,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        ctx = ctx,
                        navController = feedNavController,
                        bottomNavController = BottomNavController(
                            toFeed = BottomNavigation { onSelectTab(HomeTab.Feed) },
                            toSearch = BottomNavigation { onSelectTab(HomeTab.Search) },
                            toInbox = BottomNavigation { onSelectTab(HomeTab.Inbox) },
                            toSaved = BottomNavigation { onSelectTab(HomeTab.Saved) },
                            toProfile = BottomNavigation { onSelectTab(HomeTab.Profile) },
                        ),
                    )
                },
            )
        },
        content = {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = { padding ->
                    NavHost(
                        navController = bottomNavController,
                        startDestination = HomeTab.Feed.name,
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                    ) {
                        composable(HomeTab.Feed.name) {
                            FeedActivity(
                                accountViewModel = accountViewModel,
                                siteViewModel = siteViewModel,
                                appSettingsViewModel = appSettingsViewModel,
                                drawerState = drawerState,
                                navController = feedNavController,
                                homeViewModel = homeViewModel,
                                showVotingArrowsInListView = showVotingArrowsInListView,
                            )
                        }

                        composable(HomeTab.Search.name) {
                            CommunityListActivity(
                                accountViewModel = accountViewModel,
                                siteViewModel = siteViewModel,
                                onSelectCommunity = null,
                                navController = communityListNavController,
                            )
                        }

                        composable(HomeTab.Inbox.name) {
                            InboxActivity(
                                accountViewModel = accountViewModel,
                                siteViewModel = siteViewModel,
                                navController = inboxNavController,
                            )
                        }

                        composable(HomeTab.Saved.name) {
                            PersonProfileActivity(
                                personArg = Either.Left(account!!.id),
                                savedMode = true,
                                accountViewModel = accountViewModel,
                                siteViewModel = siteViewModel,
                                appSettingsViewModel = appSettingsViewModel,
                                showVotingArrowsInListView = showVotingArrowsInListView,
                                navController = savedAndProfileNavController,
                            )
                        }

                        composable(HomeTab.Profile.name) {
                            PersonProfileActivity(
                                personArg = Either.Left(account!!.id),
                                savedMode = false,
                                accountViewModel = accountViewModel,
                                siteViewModel = siteViewModel,
                                appSettingsViewModel = appSettingsViewModel,
                                showVotingArrowsInListView = showVotingArrowsInListView,
                                navController = savedAndProfileNavController,
                            )
                        }
                    }
                },
                bottomBar = {
                    BottomAppBarAll(
                        showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                        selectedTab = selectedTab,
                        unreadCounts = siteViewModel.getUnreadCountTotal(),
                        onSelect = onSelectTab,
                    )
                },
            )
        },
    )
}

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    navController: FeedNavController,
    bottomNavController: BottomNavController,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    ctx: Context,
    drawerState: DrawerState,
) {
    val accounts = accountViewModel.allAccounts.value
    val account = getCurrentAccount(accountViewModel)

    Drawer(
        siteRes = siteViewModel.siteRes,
        unreadCount = siteViewModel.getUnreadCountTotal(),
        accountViewModel = accountViewModel,
        isOpen = drawerState.isOpen,
        onAddAccountClick = { navController.toLogin.navigate() },
        onSwitchAccountClick = { acct ->
            accountViewModel.removeCurrent()
            accountViewModel.setCurrent(acct.id)

            fetchInitialData(
                account = acct,
                siteViewModel = siteViewModel,
            )

            fetchHomePosts(
                account = acct,
                homeViewModel = homeViewModel,
            )

            closeDrawer(scope, drawerState)
        },
        onSignOutClick = {
            accounts?.also { accts ->
                account?.also {
                    accountViewModel.delete(it)
                    val updatedList = accts.toMutableList()
                    updatedList.remove(it)

                    if (updatedList.isNotEmpty()) {
                        accountViewModel.setCurrent(updatedList[0].id)
                    }
                    fetchInitialData(
                        account = updatedList.getOrNull(0),
                        siteViewModel = siteViewModel,
                    )
                    fetchHomePosts(
                        account = updatedList.getOrNull(0),
                        homeViewModel = homeViewModel,
                    )

                    closeDrawer(scope, drawerState)
                }
            }
        },
        onClickListingType = { listingType ->
            homeViewModel.updateListingType(listingType)
            homeViewModel.resetPage()
            homeViewModel.getPosts(
                GetPosts(
                    page = homeViewModel.page,
                    sort = homeViewModel.sortType,
                    type_ = homeViewModel.listingType,
                    auth = account?.jwt,
                ),
            )
            closeDrawer(scope, drawerState)
        },
        onCommunityClick = { community ->
            navController.toCommunity.navigate(community.id)
            closeDrawer(scope, drawerState)
        },
        onClickProfile = {
            account?.id?.also {
                bottomNavController.toProfile.navigate()
                closeDrawer(scope, drawerState)
            }
        },
        onClickSaved = {
            account?.id?.also {
                bottomNavController.toSaved.navigate()
                closeDrawer(scope, drawerState)
            }
        },
        onClickInbox = {
            bottomNavController.toInbox.navigate()
            closeDrawer(scope, drawerState)
        },
        onClickSettings = {
            navController.toSettings.navigate()
            closeDrawer(scope, drawerState)
        },
        onClickCommunities = {
            bottomNavController.toSearch.navigate()
            closeDrawer(scope, drawerState)
        },
    )
}
