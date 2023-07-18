package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jerboa.api.ApiState
import com.jerboa.closeDrawer
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
            is ApiState.Success -> res.data.my_user
            else -> null
        },
        unreadCount = siteViewModel.getUnreadCountTotal(),
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
                    } else { // Could still be on a page that requires a account
                        onSelectTab(NavTab.Home)
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
