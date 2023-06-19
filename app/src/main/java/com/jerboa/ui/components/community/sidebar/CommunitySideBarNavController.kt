package com.jerboa.ui.components.community.sidebar

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgsAndDependencies

typealias ToCommunitySideBar = NavigateWithNoArgsAndDependencies

class CommunitySideBarNavController(
    override val navController: NavController,
) : NavControllerWrapper()
