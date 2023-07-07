package com.jerboa.ui.components.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.MyUserInfo
import com.jerboa.datatypes.types.Person
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.federatedNameShown
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.common.toLogin
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun Drawer(
    siteRes: ApiState<GetSiteResponse>,
    unreadCount: Int,
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onClickHome: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    isOpen: Boolean,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    if (!isOpen) showAccountAddMode = false

    val myUserInfo = when (siteRes) {
        is ApiState.Success -> siteRes.data.my_user
        else -> null
    }

    DrawerHeader(
        myPerson = myUserInfo?.local_user_view?.person,
        showAccountAddMode = showAccountAddMode,
        onClickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode },
        showAvatar = myUserInfo?.local_user_view?.local_user?.show_avatars ?: true,
    )
    // Drawer items
    DrawerContent(
        accountViewModel = accountViewModel,
        unreadCount = unreadCount,
        myUserInfo = myUserInfo,
        showAccountAddMode = showAccountAddMode,
        navController = navController,
        onSwitchAccountClick = onSwitchAccountClick,
        onSignOutClick = onSignOutClick,
        onClickListingType = onClickListingType,
        onCommunityClick = onCommunityClick,
        onClickProfile = onClickProfile,
        onClickInbox = onClickInbox,
        onClickSaved = onClickSaved,
        onClickSettings = onClickSettings,
        onClickCommunities = onClickCommunities,
        blurNSFW = blurNSFW,
        onClickHome = onClickHome,
        showBottomNav = showBottomNav,
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    navController: NavController,
    accountViewModel: AccountViewModel,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onClickHome: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    myUserInfo: MyUserInfo?,
    unreadCount: Int,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
) {
    AnimatedVisibility(
        visible = showAccountAddMode,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Divider()
        DrawerAddAccountMode(
            accountViewModel = accountViewModel,
            navController = navController,
            onSwitchAccountClick = onSwitchAccountClick,
            onSignOutClick = onSignOutClick,
        )
    }

    Divider()
    DrawerItemsMain(
        myUserInfo = myUserInfo,
        onClickListingType = onClickListingType,
        onCommunityClick = onCommunityClick,
        onClickProfile = onClickProfile,
        onClickInbox = onClickInbox,
        onClickSaved = onClickSaved,
        unreadCount = unreadCount,
        onClickSettings = onClickSettings,
        onClickCommunities = onClickCommunities,
        blurNSFW = blurNSFW,
        onClickHome = onClickHome,
        showBottomNav = showBottomNav,
    )
}

@Composable
fun DrawerItemsMain(
    myUserInfo: MyUserInfo?,
    onClickHome: () -> Unit,
    onClickSaved: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    unreadCount: Int,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
) {
    val listState = rememberLazyListState()

    val follows = myUserInfo?.follows

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState),
    ) {
        if (!follows.isNullOrEmpty()) {
            item {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_subscribed),
                    icon = Icons.Outlined.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_local),
                icon = Icons.Outlined.LocationCity,
                onClick = { onClickListingType(ListingType.Local) },
            )
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_all),
                icon = Icons.Outlined.Public,
                onClick = { onClickListingType(ListingType.All) },
            )
        }
        item {
            Divider()
        }

        if (!showBottomNav) {
            item {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.bottomBar_label_home),
                    icon = Icons.Outlined.Home,
                    onClick = onClickHome,
                )
            }
            item {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.bottomBar_label_search),
                    icon = Icons.Outlined.Search,
                    onClick = onClickCommunities,
                )
            }

            item {
                myUserInfo?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.bottomBar_label_inbox),
                        icon = Icons.Outlined.Email,
                        onClick = onClickInbox,
                        iconBadgeCount = unreadCount,
                    )
                }
            }
            item {
                myUserInfo?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.bottomBar_label_bookmarks),
                        icon = Icons.Outlined.Bookmarks,
                        onClick = onClickSaved,
                    )
                }
            }
            item {
                myUserInfo?.also {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.bottomBar_label_profile),
                        icon = Icons.Outlined.Person,
                        onClick = onClickProfile,
                    )
                }
            }
            item {
                Divider()
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_settings),
                icon = Icons.Outlined.Settings,
                onClick = onClickSettings,
            )
        }
        item {
            myUserInfo?.also {
                Divider()
            }
        }

        follows?.also { follows ->
            item {
                Text(
                    text = stringResource(R.string.home_subscriptions),
                    modifier = Modifier.padding(LARGE_PADDING),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
            items(
                follows,
                key = { follow -> follow.community.id },
            ) { follow ->
                CommunityLinkLarger(
                    community = follow.community,
                    onClick = onCommunityClick,
                    showDefaultIcon = true,
                    blurNSFW = blurNSFW,
                )
            }
        }
    }
}

@Preview
@Composable
fun DrawerItemsMainPreview() {
    DrawerItemsMain(
        myUserInfo = null,
        onClickListingType = {},
        onClickProfile = {},
        onClickInbox = {},
        onCommunityClick = {},
        onClickSaved = {},
        onClickSettings = {},
        onClickCommunities = {},
        onClickHome = {},
        unreadCount = 2,
        blurNSFW = true,
        showBottomNav = false,
    )
}

@Composable
fun DrawerAddAccountMode(
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
) {
    val accountsWithoutCurrent = accountViewModel?.allAccounts?.value?.toMutableList()
    val currentAccount = accountsWithoutCurrent?.firstOrNull { it.current }
    accountsWithoutCurrent?.remove(currentAccount)

    Column {
        IconAndTextDrawerItem(
            text = stringResource(R.string.home_add_account),
            icon = Icons.Outlined.Add,
            onClick = { navController.toLogin() },
        )
        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_switch_to, it.name, it.instance),
                icon = Icons.Outlined.Login,
                onClick = { onSwitchAccountClick(it) },
            )
        }
        currentAccount?.also {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_sign_out),
                icon = Icons.Outlined.Close,
                onClick = onSignOutClick,
            )
        }
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    DrawerAddAccountMode(
        onSignOutClick = {},
        onSwitchAccountClick = {},
        accountViewModel = null,
    )
}

@Composable
fun DrawerHeader(
    myPerson: Person?,
    onClickShowAccountAddMode: () -> Unit,
    showAccountAddMode: Boolean = false,
    showAvatar: Boolean,
) {
    val sizeMod = Modifier
        .fillMaxWidth()
        .height(DRAWER_BANNER_SIZE)

    Box(
        modifier = sizeMod
            .clickable(onClick = onClickShowAccountAddMode),
    ) {
        myPerson?.banner?.also {
            PictrsBannerImage(
                url = it,
            )
        }
        // banner
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = sizeMod
                .padding(XL_PADDING),
        ) {
            Box(modifier = Modifier.weight(0.9f)) {
                AvatarAndAccountName(myPerson, showAvatar)
            }
            Icon(
                modifier = Modifier.weight(0.1f),
                imageVector = if (showAccountAddMode) {
                    Icons.Outlined.ExpandLess
                } else {
                    Icons.Outlined.ExpandMore
                },
                contentDescription = if (showAccountAddMode) {
                    stringResource(R.string.moreOptions)
                } else {
                    stringResource(R.string.lessOptions)
                },
            )
        }
    }
}

@Composable
fun AvatarAndAccountName(myPerson: Person?, showAvatar: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        if (showAvatar) {
            myPerson?.avatar?.also {
                LargerCircularIcon(icon = it)
            }
        }
        Column() {
            PersonName(
                person = myPerson,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = myPerson?.let { federatedNameShown(it) } ?: "",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        }
    }
}

@Preview
@Composable
fun AvatarAndAccountNamePreview() {
    AvatarAndAccountName(myPerson = samplePerson, showAvatar = true)
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    DrawerHeader(
        myPerson = samplePerson,
        onClickShowAccountAddMode = {},
        showAvatar = true,
    )
}
