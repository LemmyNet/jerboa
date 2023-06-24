package com.jerboa.ui.components.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jerboa.datatypes.types.GetPersonDetails
import com.jerboa.datatypes.types.GetPersonMentions
import com.jerboa.datatypes.types.GetPrivateMessages
import com.jerboa.datatypes.types.GetReplies
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.loginFirstToast
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyViewModel

enum class BottomNavTab {
    Home, Search, Inbox, Saved, Profile;

    fun needsLogin() = this == Inbox || this == Saved || this == Profile
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun BottomNavActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings?,
    communityListViewModel: CommunityListViewModel,
    inboxViewModel: InboxViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    commentEditViewModel: CommentEditViewModel,
    personProfileViewModel: PersonProfileViewModel,
    privateMessageReplyViewModel: PrivateMessageReplyViewModel,
) {
    val account = getCurrentAccount(accountViewModel)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val bottomNavController = rememberAnimatedNavController()
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.Home) }
    val onSelectTab = { tab: BottomNavTab ->
        if (tab.needsLogin() && account == null) {
            loginFirstToast(ctx)
        } else {
            selectedTab = tab
            bottomNavController.navigate(tab.name) {
                launchSingleTop = true
                popUpTo(0) // To make back button close the app.
            }
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        gesturesEnabled = selectedTab == BottomNavTab.Home,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    MainDrawer(
                        siteViewModel = siteViewModel,
                        navController = navController,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        onSelectTab = if (appSettings?.showBottomNav == true) onSelectTab else null,
                    )
                },
            )
        },
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        content = {
            Scaffold(
                bottomBar = {
                    BottomAppBarAll(
                        showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                        selectedTab = selectedTab,
                        unreadCounts = siteViewModel.getUnreadCountTotal(),
                        onSelect = onSelectTab,
                    )
                },
            ) { padding ->
                val bottomPadding =
                    if (selectedTab == BottomNavTab.Search && WindowInsets.isImeVisible) {
                        0.dp
                    } else {
                        padding.calculateBottomPadding()
                    }

                AnimatedNavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavTab.Home.name,
                    modifier = Modifier.padding(bottom = bottomPadding),
                ) {
                    composable(route = BottomNavTab.Home.name) {
                        HomeActivity(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                                ?: true,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                            drawerState = drawerState,
                        )
                    }

                    composable(route = BottomNavTab.Search.name) {
                        LaunchedEffect(Unit) {
                            // Whenever navigating here, reset the list with your followed communities
                            communityListViewModel.setCommunityListFromFollowed(siteViewModel)
                        }

                        CommunityListActivity(
                            navController = navController,
                            accountViewModel = accountViewModel,
                            communityListViewModel = communityListViewModel,
                            selectMode = it.arguments?.getBoolean("select")!!,
                        )
                    }

                    composable(route = BottomNavTab.Inbox.name) {
                        if (account != null) {
                            LaunchedEffect(Unit) {
                                inboxViewModel.resetPage()
                                inboxViewModel.getReplies(
                                    GetReplies(
                                        auth = account.jwt,
                                    ),
                                )
                                inboxViewModel.getMentions(
                                    GetPersonMentions(
                                        auth = account.jwt,
                                    ),
                                )
                                inboxViewModel.getMessages(
                                    GetPrivateMessages(
                                        auth = account.jwt,
                                    ),
                                )
                            }
                        }

                        InboxActivity(
                            navController = navController,
                            inboxViewModel = inboxViewModel,
                            accountViewModel = accountViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            siteViewModel = siteViewModel,
                            privateMessageReplyViewModel = privateMessageReplyViewModel,
                        )
                    }

                    composable(route = BottomNavTab.Saved.name) {
                        val savedMode = true
                        LaunchedEffect(Unit) {
                            val personId = account?.id!!

                            personProfileViewModel.resetPage()
                            personProfileViewModel.getPersonDetails(
                                GetPersonDetails(
                                    person_id = personId,
                                    sort = SortType.New,
                                    auth = account.jwt,
                                    saved_only = savedMode,
                                ),
                            )
                        }

                        PersonProfileActivity(
                            savedMode = savedMode,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            accountViewModel = accountViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                                ?: true,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                        )
                    }

                    composable(route = BottomNavTab.Profile.name) {
                        val savedMode = false
                        LaunchedEffect(Unit) {
                            val personId = account?.id!!

                            personProfileViewModel.resetPage()
                            personProfileViewModel.getPersonDetails(
                                GetPersonDetails(
                                    person_id = personId,
                                    sort = SortType.New,
                                    auth = account.jwt,
                                    saved_only = savedMode,
                                ),
                            )
                        }

                        PersonProfileActivity(
                            savedMode = savedMode,
                            navController = navController,
                            personProfileViewModel = personProfileViewModel,
                            accountViewModel = accountViewModel,
                            commentEditViewModel = commentEditViewModel,
                            commentReplyViewModel = commentReplyViewModel,
                            postEditViewModel = postEditViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                                ?: true,
                            siteViewModel = siteViewModel,
                            useCustomTabs = appSettings?.useCustomTabs ?: true,
                            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
                        )
                    }
                }
            }
        },
    )
}
