package com.jerboa.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

val noEnterTransition = fadeIn(tween(0))
val noPopExitTransition = fadeOut(tween(0))

val defaultEnterTransition = slideInHorizontally(tween(300)) { w -> w }
val defaultExitTransition = slideOutHorizontally(tween(300)) { w -> -w }
val defaultPopEnterTransition = slideInHorizontally(tween(300)) { w -> -w }
val defaultPopExitTransition = slideOutHorizontally(tween(300)) { w -> w }

fun NavController.canPop() = previousBackStackEntry != null
