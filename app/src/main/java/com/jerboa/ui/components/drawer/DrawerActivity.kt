package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jerboa.api.ApiState
import com.jerboa.closeDrawer
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.fetchHomePosts
import com.jerboa.fetchInitialData
import com.jerboa.model.AccountViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.NavTab
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onSettingsClick: () -> Unit,
    onCommunityClick: (Int) -> Unit,
    onClickLogin: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
) {
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accountViewModel)

    BackHandler(drawerState.isOpen) {
        closeDrawer(scope, drawerState)
    }

    Drawer(
        myUserInfo = when (val res = siteViewModel.siteRes) {
            is ApiState.Success -> {
                // JWT Failed
                if (!account.isAnon() && account.isReady() && res.data.my_user == null) {
                    accountViewModel.invalidateAccount(account)
                }
                res.data.my_user
            }
            is ApiState.Failure -> {
                // Invalidate account
                if (account.isReady()) {
                    accountViewModel.invalidateAccount(account)
                }

                null
            }
            else -> null
        },
        unreadCount = siteViewModel.unreadCount,
        accountViewModel = accountViewModel,
        onAddAccount = onClickLogin,
        isOpen = drawerState.isOpen,
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

            onSelectTab(NavTab.Home)
            closeDrawer(scope, drawerState)
        },
        onSignOutClick = {
            accountViewModel.deleteAccountAndSwapCurrent(
                account,
                siteViewModel,
                homeViewModel,
            )

            onSelectTab(NavTab.Home)
            closeDrawer(scope, drawerState)
        },
        onSwitchAnon = {
            if (!account.isAnon()) {
                accountViewModel.removeCurrent()

                fetchInitialData(
                    account = AnonAccount,
                    siteViewModel = siteViewModel,
                )
                fetchHomePosts(
                    account = AnonAccount,
                    homeViewModel = homeViewModel,
                )

                onSelectTab(NavTab.Home)
                closeDrawer(scope, drawerState)
            }
        },
        onClickListingType = { listingType ->
            homeViewModel.updateListingType(listingType)
            homeViewModel.resetPosts(account)
            closeDrawer(scope, drawerState)
        },
        onCommunityClick = { community ->
            onCommunityClick(community.id)
            closeDrawer(scope, drawerState)
        },
        onClickSettings = {
            onSettingsClick()
            closeDrawer(scope, drawerState)
        },
        blurNSFW = blurNSFW,
        showBottomNav = showBottomNav,
        onSelectTab = onSelectTab,
        closeDrawer = { closeDrawer(scope, drawerState) },
    )
}
