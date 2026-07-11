package com.jerboa.ui.components.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import com.jerboa.api.ApiState
import com.jerboa.api.toOpt
import com.jerboa.closeDrawer
import com.jerboa.datatypes.UserViewType
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.feat.BlurNSFW
import com.jerboa.model.AccountViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.MyUserInfoViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.NavTab
import it.vercruysse.lemmyapi.datatypes.CommunityId
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainDrawer(
    myUserInfoViewModel: MyUserInfoViewModel,
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
            when (val res = myUserInfoViewModel.myUserRes) {
                is ApiState.Success -> {
                    // JWT Failed
                    if (!account.isAnon() && account.isReady()) {
                        accountViewModel.invalidateAccount(account)
                    }
                    res.data
                }

                is ApiState.Failure -> {
                    // Invalidate account
                    if (account.isReady()) {
                        accountViewModel.invalidateAccount(account)
                    }

                    null
                }

                else -> {
                    null
                }
            },
        follows = myUserInfoViewModel.myUserRes.toOpt()?.follows
            ?.sortedBy { (it.community.title ?: it.community.name).lowercase() }
            .orEmpty(),

        unreadNotificationCount = myUserInfoViewModel.unreadCountsRes.toOpt()?.notification_count ?: 0,
        unreadAppCount = myUserInfoViewModel.unreadCountsRes.toOpt()?.registration_application_count,
        unreadReportCount = myUserInfoViewModel.unreadCountsRes.toOpt()?.report_count,
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
