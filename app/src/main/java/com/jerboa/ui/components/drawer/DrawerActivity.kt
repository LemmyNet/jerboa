package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jerboa.api.ApiState
import com.jerboa.closeDrawer
import com.jerboa.datatypes.types.CommunityFollowerView
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.model.AccountViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.NavTab
import kotlinx.collections.immutable.toImmutableList
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
    val account = getCurrentAccount(accountViewModel)

    var follows by remember { mutableStateOf(listOf<CommunityFollowerView>()) }

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
                follows = res.data.my_user?.follows?.sortedBy { it.community.name }.orEmpty()
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
        follows = follows.toImmutableList(),
        unreadCount = siteViewModel.unreadCount,
        accountViewModel = accountViewModel,
        onAddAccount = onClickLogin,
        isOpen = drawerState.isOpen,
        onSwitchAccountClick = { acct ->
            accountViewModel.removeCurrent()
            accountViewModel.setCurrent(acct.id)

            onSelectTab(NavTab.Home)
            closeDrawer(scope, drawerState)
        },
        onSignOutClick = {
            accountViewModel.deleteAccountAndSwapCurrent(account)

            onSelectTab(NavTab.Home)
            closeDrawer(scope, drawerState)
        },
        onSwitchAnon = {
            if (!account.isAnon()) {
                accountViewModel.removeCurrent()

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
