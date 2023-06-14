package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.nav.canPop

@Composable
fun DefaultBackButton(navController: NavController) {
    var canPop by remember { mutableStateOf(false) }
    navController.addOnDestinationChangedListener { controller, _, _ ->
        canPop = controller.canPop()
    }

    var pressedBackButton by remember { mutableStateOf(false) }

    if (canPop || pressedBackButton) {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            pressedBackButton = true
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.topAppBar_back)
            )
        }
    }
}
