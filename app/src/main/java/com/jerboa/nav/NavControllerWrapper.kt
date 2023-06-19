package com.jerboa.nav

import androidx.navigation.NavController

abstract class NavControllerWrapper {
    protected abstract val navController: NavController

    fun canPop() = navController.previousBackStackEntry != null

    fun navigateUp() = navController.navigateUp()
}
