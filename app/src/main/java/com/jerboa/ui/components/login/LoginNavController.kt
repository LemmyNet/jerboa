package com.jerboa.ui.components.login

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgsAndDependencies
import com.jerboa.ui.components.home.ToHome

typealias ToLogin = NavigateWithNoArgsAndDependencies

class LoginNavController(
    override val navController: NavController,
    val toHome: ToHome,
) : NavControllerWrapper()
