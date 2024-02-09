package com.jerboa.ui.components.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.datatypes.UserViewType
import com.jerboa.db.entity.AppSettings
import com.jerboa.db.entity.isReady
import com.jerboa.db.entity.userViewType
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.toEnum
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.drawer.MainDrawer
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.registrationapplications.RegistrationApplicationsActivity
import com.jerboa.ui.components.reports.ReportsActivity
import kotlinx.coroutines.launch

enum class NavTab(
    val textId: Int,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val contentDescriptionId: Int,
    val userViewType: UserViewType,
) {
    Home(
        textId = R.string.bottomBar_label_home,
        iconOutlined = Icons.Outlined.Home,
        iconFilled = Icons.Filled.Home,
        contentDescriptionId = R.string.bottomBar_home,
        userViewType = UserViewType.Normal,
    ),
    Search(
        textId = R.string.bottomBar_label_search,
        iconOutlined = Icons.Outlined.Search,
        iconFilled = Icons.Filled.Search,
        contentDescriptionId = R.string.bottomBar_search,
        userViewType = UserViewType.Normal,
    ),
    Inbox(
        textId = R.string.bottomBar_label_inbox,
        iconOutlined = Icons.Outlined.Email,
        iconFilled = Icons.Filled.Email,
        contentDescriptionId = R.string.bottomBar_inbox,
        userViewType = UserViewType.Normal,
    ),
    RegistrationApplications(
        R.string.applications_request_shorthand,
        Icons.Outlined.AppRegistration,
        Icons.Filled.AppRegistration,
        R.string.bottomBar_registrations,
        userViewType = UserViewType.AdminOnly,
    ),
    Reports(
        R.string.reports,
        Icons.Outlined.Flag,
        Icons.Filled.Flag,
        R.string.bottomBar_reports,
        userViewType = UserViewType.AdminOrMod,
    ),
    Saved(
        textId = R.string.bottomBar_label_bookmarks,
        iconOutlined = Icons.Outlined.Bookmarks,
        iconFilled = Icons.Filled.Bookmarks,
        contentDescriptionId = R.string.bottomBar_bookmarks,
        userViewType = UserViewType.Normal,
    ),
    Profile(
        textId = R.string.bottomBar_label_profile,
        iconOutlined = Icons.Outlined.Person,
        iconFilled = Icons.Filled.Person,
        contentDescriptionId = R.string.bottomBar_profile,
        userViewType = UserViewType.Normal,
    ),
    ;

    fun needsLogin() = this == Inbox || this == Saved || this == Profile || this == RegistrationApplications || this == Reports
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun BottomNavActivity(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings,
    drawerState: DrawerState,
) {
    val account = getCurrentAccount(accountViewModel)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    val bottomNavController = rememberNavController()
    val snackbarHostState = remember(account) { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableStateOf(NavTab.Home) }

    val onInnerSelectTab = { tab: NavTab ->
        selectedTab = tab
        val currentRoute = bottomNavController.currentDestination?.route
        if (currentRoute == tab.name && tab == NavTab.Home) {
            scope.launch {
                homeViewModel.lazyListState.animateScrollToItem(0)
            }
        } else {
            bottomNavController.navigate(tab.name) {
                launchSingleTop = true
                popUpTo(bottomNavController.graph.id) // To make back button close the app.
            }
        }
    }

    val onSelectTab: (NavTab) -> Unit = { tab: NavTab ->
        if (tab.needsLogin()) {
            account.doIfReadyElseDisplayInfo(
                appState,
                ctx,
                snackbarHostState,
                scope,
                siteViewModel,
                accountViewModel,
                loginAsToast = false,
            ) {
                onInnerSelectTab(tab)
            }
        } else {
            onInnerSelectTab(tab)
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
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        onSelectTab = onSelectTab,
                        blurNSFW = appSettings.blurNSFW,
                        showBottomNav = appSettings.showBottomNav,
                        onCommunityClick = appState::toCommunity,
                        onSettingsClick = appState::toSettings,
                        onClickLogin = appState::toLogin,
                    )
                },
            )
        },
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        content = {
            Scaffold(
                snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (appSettings.showBottomNav && account.isReady()) {
                        BottomAppBarAll(
                            selectedTab = selectedTab,
                            unreadCounts = siteViewModel.unreadCount,
                            unreadAppCount = siteViewModel.unreadAppCount,
                            unreadReportCount = siteViewModel.unreadReportCount,
                            showTextDescriptionsInNavbar = appSettings.showTextDescriptionsInNavbar,
                            userViewType = account.userViewType(),
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
                            appState = appState,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            drawerState = drawerState,
                            blurNSFW = appSettings.blurNSFW,
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            postActionbarMode = appSettings.postActionbarMode,
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                        )
                    }

                    composable(route = NavTab.Search.name) {
                        CommunityListActivity(
                            appState = appState,
                            selectMode = false,
                            followList = siteViewModel.getFollowList(),
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = NavTab.Inbox.name) {
                        InboxActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            blurNSFW = appSettings.blurNSFW,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = NavTab.RegistrationApplications.name) {
                        RegistrationApplicationsActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = NavTab.Reports.name) {
                        ReportsActivity(
                            appState = appState,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            drawerState = drawerState,
                            blurNSFW = appSettings.blurNSFW,
                        )
                    }

                    composable(route = NavTab.Saved.name) {
                        PersonProfileActivity(
                            personArg = Either.Left(account.id),
                            savedMode = true,
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

                    composable(route = NavTab.Profile.name) {
                        PersonProfileActivity(
                            personArg = Either.Left(account.id),
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
                }
            }
        },
    )
}
