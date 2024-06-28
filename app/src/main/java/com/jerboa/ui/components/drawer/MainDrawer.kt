package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import com.jerboa.api.ApiState
import com.jerboa.closeDrawer
import com.jerboa.datatypes.UserViewType
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.feat.BlurNSFW
import com.jerboa.model.AccountViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.NavTab
import it.vercruysse.lemmyapi.datatypes.CommunityId
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onSettingsClick: () -> Unit,
    onCommunityClick: (CommunityId) -> Unit,
    onClickLogin: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    blurNSFW: BlurNSFW,
    showBottomNav: Boolean,
    userViewType: UserViewType,
) {
    val account = getCurrentAccount(accountViewModel)

    BackHandler(drawerState.isOpen) {
        closeDrawer(scope, drawerState)
    }

    Drawer(
        myUserInfo =
            when (val res = siteViewModel.siteRes) {
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
        follows = when (val res = siteViewModel.siteRes) {
            is ApiState.Success -> {
                res.data.my_user
                    ?.follows
                    ?.sortedBy { it.community.title.lowercase() }
                    .orEmpty()
            }
            else -> emptyList()
        },
        unreadCount = siteViewModel.unreadCount,
        unreadAppCount = siteViewModel.unreadAppCount,
        unreadReportCount = siteViewModel.unreadReportCount,
        accountViewModel = accountViewModel,
        onAddAccount = onClickLogin,
        isOpen = drawerState.isOpen,
        onSwitchAccountClick = { acct ->
            accountViewModel.updateCurrent(acct).invokeOnCompletion {
                onSelectTab(NavTab.Home)
                closeDrawer(scope, drawerState)
            }
        },
        onSignOutClick = {
            accountViewModel.deleteAccountAndSwapCurrent(account).invokeOnCompletion {
                onSelectTab(NavTab.Home)
                closeDrawer(scope, drawerState)
            }
        },
        onSwitchAnon = {
            if (!account.isAnon()) {
                accountViewModel.removeCurrent(true).invokeOnCompletion {
                    onSelectTab(NavTab.Home)
                    closeDrawer(scope, drawerState)
                }
            }
        },
        onClickListingType = { listingType ->
            homeViewModel.updateListingType(listingType)
            homeViewModel.resetPosts()
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
        userViewType = userViewType,
    )
}
