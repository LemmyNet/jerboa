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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.CommunityFollowerView
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.MyUserInfo
import com.jerboa.datatypes.types.Person
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AccountViewModelFactory
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.home.NavTab
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import com.jerboa.ui.theme.muted
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Drawer(
    myUserInfo: MyUserInfo?,
    follows: ImmutableList<CommunityFollowerView>,
    unreadCount: Int,
    accountViewModel: AccountViewModel,
    onAddAccount: () -> Unit,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onSwitchAnon: () -> Unit,
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
        accountViewModel = accountViewModel,
        myPerson = myUserInfo?.local_user_view?.person,
        showAccountAddMode = showAccountAddMode,
        onClickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode },
        showAvatar = myUserInfo?.local_user_view?.local_user?.show_avatars ?: true,
    )
    // Drawer items
    DrawerContent(
        accountViewModel = accountViewModel,
        follows = follows,
        unreadCount = unreadCount,
        myUserInfo = myUserInfo,
        showAccountAddMode = showAccountAddMode,
        onAddAccount = onAddAccount,
        onSwitchAccountClick = onSwitchAccountClick,
        onSignOutClick = onSignOutClick,
        onClickListingType = onClickListingType,
        onCommunityClick = onCommunityClick,
        onClickSettings = onClickSettings,
        blurNSFW = blurNSFW,
        showBottomNav = showBottomNav,
        closeDrawer = closeDrawer,
        onSelectTab = onSelectTab,
        onSwitchAnon = onSwitchAnon,
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    follows: ImmutableList<CommunityFollowerView>,
    onAddAccount: () -> Unit,
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
    onSwitchAnon: () -> Unit,
) {
    AnimatedVisibility(
        visible = showAccountAddMode,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Divider()
        DrawerAddAccountMode(
            accountViewModel = accountViewModel,
            onAddAccount = onAddAccount,
            onSwitchAccountClick = onSwitchAccountClick,
            onSignOutClick = onSignOutClick,
            onSwitchAnon = onSwitchAnon,
        )
    }

    Divider()
    DrawerItemsMain(
        myUserInfo = myUserInfo,
        follows = follows,
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
    follows: ImmutableList<CommunityFollowerView>,
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

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState),
    ) {
        if (follows.isNotEmpty()) {
            item {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_subscribed),
                    icon = Icons.Outlined.Bookmarks,
                    onClick = {
                        onClickListingType(ListingType.Subscribed)
                        onSelectTab(NavTab.Home)
                    },
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_local),
                icon = Icons.Outlined.LocationCity,
                onClick = {
                    onClickListingType(ListingType.Local)
                    onSelectTab(NavTab.Home)
                },
            )
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_all),
                icon = Icons.Outlined.Public,
                onClick = {
                    onClickListingType(ListingType.All)
                    onSelectTab(NavTab.Home)
                },
            )
        }
        item {
            Divider()
        }

        if (!showBottomNav) {
            items(NavTab.entries) {
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

            item(contentType = "divider") {
                Divider()
            }
        }
        item("settings") {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_settings),
                icon = Icons.Outlined.Settings,
                onClick = onClickSettings,
            )
        }
        myUserInfo?.also {
            item(contentType = "divider") {
                Divider()
            }
        }

        follows.also { follows ->
            item(contentType = "SubscriptionHeader") {
                Text(
                    text = stringResource(R.string.home_subscriptions),
                    modifier = Modifier.padding(LARGE_PADDING),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
            items(
                follows,
                key = { follow -> follow.community.id },
                contentType = { "CommunityLink" },
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
        follows = persistentListOf(),
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
    accountViewModel: AccountViewModel,
    onAddAccount: () -> Unit,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onSwitchAnon: () -> Unit,
) {
    val allAccounts = accountViewModel.allAccounts.observeAsState()
    val accountsWithoutCurrent = allAccounts.value?.toMutableList()
    val currentAccount = getCurrentAccount(accountViewModel = accountViewModel)

    accountsWithoutCurrent?.remove(currentAccount)

    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (!currentAccount.isAnon()) {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_sign_out),
                icon = Icons.Outlined.Close,
                onClick = onSignOutClick,
            )

            IconAndTextDrawerItem(
                text = stringResource(R.string.home_switch_anon),
                icon = Icons.Outlined.Login,
                onClick = onSwitchAnon,
            )
        }

        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_switch_to, it.name, it.instance),
                icon = Icons.Outlined.Login,
                onClick = { onSwitchAccountClick(it) },
            )
        }

        IconAndTextDrawerItem(
            text = stringResource(R.string.home_add_account),
            icon = Icons.Outlined.Add,
            onClick = onAddAccount,
        )
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    val accountViewModel: AccountViewModel = viewModel(factory = AccountViewModelFactory.Factory)
    DrawerAddAccountMode(
        onAddAccount = {},
        onSignOutClick = {},
        onSwitchAccountClick = {},
        accountViewModel = accountViewModel,
        onSwitchAnon = {},
    )
}

@Composable
fun DrawerHeader(
    accountViewModel: AccountViewModel,
    myPerson: Person?,
    onClickShowAccountAddMode: () -> Unit,
    showAccountAddMode: Boolean = false,
    showAvatar: Boolean,
) {
    val account = getCurrentAccount(accountViewModel)
    val showWarningIcon = !account.isAnon() && !account.isReady()

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
            if (showWarningIcon) {
                Icon(
                    modifier = Modifier
                        .weight(0.1f)
                        .padding(end = SMALL_PADDING),
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = stringResource(R.string.warning),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
            Box(modifier = Modifier.weight(if (showWarningIcon) 0.8f else 0.9f)) {
                AvatarAndAccountName(account, myPerson, showAvatar)
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
fun AvatarAndAccountName(account: Account, myPerson: Person?, showAvatar: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        if (showAvatar) {
            myPerson?.avatar?.also {
                LargerCircularIcon(icon = it)
            }
        }
        Column {
            PersonName(
                name = myPerson?.display_name ?: account.name,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (account.isAnon()) "" else "${account.name}@${account.instance}",
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
    AvatarAndAccountName(account = AnonAccount, myPerson = samplePerson, showAvatar = true)
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    val accountViewModel: AccountViewModel = viewModel(factory = AccountViewModelFactory.Factory)
    DrawerHeader(
        myPerson = samplePerson,
        onClickShowAccountAddMode = {},
        showAvatar = true,
        accountViewModel = accountViewModel,
    )
}
