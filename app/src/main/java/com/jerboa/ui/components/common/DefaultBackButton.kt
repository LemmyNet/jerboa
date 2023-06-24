package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R

@Composable
fun DefaultBackButton(navController: NavController) {
    val canPop = remember { navController.previousBackStackEntry != null }

    if (canPop) {
        IconButton(onClick = navController::navigateUp) {
            Icon(
                Icons.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.community_back),
            )
        }
    }
}
