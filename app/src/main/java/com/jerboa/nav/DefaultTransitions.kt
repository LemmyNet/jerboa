package com.jerboa.nav

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

val enterTransition = slideInHorizontally { it }
val exitTransition = slideOutHorizontally { -it }
val popEnterTransition = slideInHorizontally { -it }
val popExitTransition = slideOutHorizontally { it }
