package com.jerboa.ui.components.home

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgsAndDependencies
import com.jerboa.ui.components.community.ToCommunity
import com.jerboa.ui.components.login.ToLogin
import com.jerboa.ui.components.person.ToProfile
import com.jerboa.ui.components.post.ToPost
import com.jerboa.ui.components.post.create.ToCreatePost
import com.jerboa.ui.components.post.edit.ToPostEdit
import com.jerboa.ui.components.report.ToPostReport
import com.jerboa.ui.components.settings.ToSettings

typealias ToHome = NavigateWithNoArgsAndDependencies
typealias ToSiteSideBar = NavigateWithNoArgsAndDependencies

class FeedNavController(
    override val navController: NavController,
    val toPostEdit: ToPostEdit,
    val toCommunity: ToCommunity,
    val toProfile: ToProfile,
    val toSettings: ToSettings,
    val toCreatePost: ToCreatePost,
    val toPost: ToPost,
    val toPostReport: ToPostReport,
    val toLogin: ToLogin,
    val toSiteSideBar: ToSiteSideBar,
) : NavControllerWrapper()

typealias BottomNavigation = NavigateWithNoArgsAndDependencies

class BottomNavController(
    val toFeed: BottomNavigation,
    val toSearch: BottomNavigation,
    val toInbox: BottomNavigation,
    val toSaved: BottomNavigation,
    val toProfile: BottomNavigation,
)
