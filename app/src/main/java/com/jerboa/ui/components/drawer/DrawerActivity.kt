package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.closeDrawer
import com.jerboa.db.AccountViewModel
import com.jerboa.fetchHomePosts
import com.jerboa.fetchInitialData
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.toCommunity
import com.jerboa.ui.components.common.toHome
import com.jerboa.ui.components.common.toSettings
import com.jerboa.ui.components.home.NavTab
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    navController: NavController,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onSelectTab: (NavTab) -> Unit,
    blurNSFW: Boolean,
    showBottomNave: Boolean,
) {
    val accounts = accountViewModel.allAccounts.value
    val account = getCurrentAccount(accountViewModel)

    BackHandler(drawerState.isOpen) {
        closeDrawer(scope, drawerState)
    }

    Drawer(
        siteRes = siteViewModel.siteRes,
        unreadCount = siteViewModel.getUnreadCountTotal(),
        accountViewModel = accountViewModel,
        navController = navController,
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
                        navController.toHome()
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
            navController.toCommunity(id = community.id)
            closeDrawer(scope, drawerState)
        },
        onClickSettings = {
            navController.toSettings()
            closeDrawer(scope, drawerState)
        },
        blurNSFW = blurNSFW,
        showBottomNav = showBottomNave,
        onSelectTab = onSelectTab,
        closeDrawer = { closeDrawer(scope, drawerState) },
    )
}
