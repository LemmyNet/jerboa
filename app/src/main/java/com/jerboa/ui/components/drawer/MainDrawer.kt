package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import com.jerboa.closeDrawer
import com.jerboa.db.entity.AppSettings
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.NavTab
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.UnreadCountsResponse
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainDrawer(
    myUserInfo: MyUserInfo?,
    unreadCounts: UnreadCountsResponse?,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    appSettings: AppSettings,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onSettingsClick: () -> Unit,
    onCommunityClick: (CommunityId) -> Unit,
    onClickLogin: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
) {
    val account = getCurrentAccount(accountViewModel)

    BackHandler(drawerState.isOpen) {
        closeDrawer(scope, drawerState)
    }

    Drawer(
        myUserInfo = myUserInfo,
        unreadCounts = unreadCounts,
        accountViewModel = accountViewModel,
        appSettings = appSettings,
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
            if (myUserInfo != null) {
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
        onSelectTab = onSelectTab,
        closeDrawer = { closeDrawer(scope, drawerState) },
    )
}
