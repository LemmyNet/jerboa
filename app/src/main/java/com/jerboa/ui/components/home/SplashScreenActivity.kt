package com.jerboa.ui.components.home

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.LAUNCH_DELAY
import com.jerboa.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreenActivity(
    navController: NavController
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        val startRoute = "home"

        val scale = remember {
            Animatable(0f)
        }

        // AnimationEffect
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    }
                )
            )

            delay(LAUNCH_DELAY)
            navController.navigate(startRoute)
        }

        // Image
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_jerboa),
                contentDescription = "Logo",
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenActivityPreview() {
    SplashScreenActivity(navController = rememberNavController())
}
