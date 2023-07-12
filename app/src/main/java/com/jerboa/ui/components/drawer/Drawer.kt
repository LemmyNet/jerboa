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
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.MyUserInfo
import com.jerboa.datatypes.types.Person
import com.jerboa.db.entity.Account
import com.jerboa.federatedNameShown
import com.jerboa.model.AccountViewModel
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.common.toLogin
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.home.NavTab
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun Drawer(
    myUserInfo: MyUserInfo?,
    unreadCount: Int,
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onClickSettings: () -> Unit,
    isOpen: Boolean,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
    closeDrawer: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    if (!isOpen) showAccountAddMode = false

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
        onClickSettings = onClickSettings,
        blurNSFW = blurNSFW,
        showBottomNav = showBottomNav,
        closeDrawer = closeDrawer,
        onSelectTab = onSelectTab,
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
    onClickSettings: () -> Unit,
    myUserInfo: MyUserInfo?,
    unreadCount: Int,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
    closeDrawer: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
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
        unreadCount = unreadCount,
        onClickSettings = onClickSettings,
        blurNSFW = blurNSFW,
        showBottomNav = showBottomNav,
        onSelectTab = onSelectTab,
        closeDrawer = closeDrawer,
    )
}

@Composable
fun DrawerItemsMain(
    myUserInfo: MyUserInfo?,
    onClickSettings: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    unreadCount: Int,
    blurNSFW: Boolean,
    showBottomNav: Boolean,
    closeDrawer: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
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
            items(NavTab.values()) {
                IconAndTextDrawerItem(
                    text = stringResource(it.textId),
                    icon = it.iconOutlined,
                    onClick = {
                        onSelectTab(it)
                        closeDrawer()
                    },
                    iconBadgeCount = if (it == NavTab.Inbox) unreadCount else null,
                    contentDescription = stringResource(id = it.contentDescriptionId),
                )
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
        onCommunityClick = {},
        onClickSettings = {},
        unreadCount = 2,
        blurNSFW = true,
        showBottomNav = false,
        closeDrawer = {},
        onSelectTab = {},
    )
}

@Composable
fun DrawerAddAccountMode(
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
) {
    val allAccounts = accountViewModel?.allAccounts?.observeAsState()
    val accountsWithoutCurrent = allAccounts?.value?.toMutableList()
    val currentAccount = accountViewModel?.let { getCurrentAccount(accountViewModel = it) }

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
