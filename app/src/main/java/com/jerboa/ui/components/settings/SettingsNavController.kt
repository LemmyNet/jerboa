package com.jerboa.ui.components.settings

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgsAndDependencies

typealias ToSettings = NavigateWithNoArgsAndDependencies
typealias ToAccountSettings = NavigateWithNoArgsAndDependencies
typealias ToLookAndFeel = NavigateWithNoArgsAndDependencies
typealias ToAbout = NavigateWithNoArgsAndDependencies

class SettingsNavController(
    override val navController: NavController,
    val toLookAndFeel: ToLookAndFeel,
    val toAccountSettings: ToAccountSettings,
    val toAbout: ToAbout,
) : NavControllerWrapper()

class LookAndFeelNavController(
    override val navController: NavController,
) : NavControllerWrapper()

class AccountSettingsNavController(
    override val navController: NavController,
) : NavControllerWrapper()
