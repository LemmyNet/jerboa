package com.jerboa.ui.components.home

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.jerboa.LAUNCH_DELAY
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import kotlinx.coroutines.delay

@Composable
fun SplashScreenActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {

    val startRoute = "home"
    FetchInitialData(accountViewModel, siteViewModel, homeViewModel)

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
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

@Composable
private fun FetchInitialData(
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    homeViewModel: HomeViewModel
) {
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    if (account != null) {
        API.changeLemmyInstance(account.instance)
        siteViewModel.fetchSite(auth = account.jwt)

        // Use to get your users default sort and listing types
        siteViewModel.siteRes?.my_user?.also { myUser ->
            homeViewModel.fetchPosts(
                account = account,
                changeListingType = ListingType.values()[
                    myUser.local_user_view.local_user
                        .default_listing_type
                ],
                changeSortType = SortType.values()[
                    myUser.local_user_view.local_user
                        .default_sort_type
                ],
                clear = true,
            )
            homeViewModel.fetchUnreadCounts(account = account)
        }
    } else {
        // An anonymous user
        siteViewModel.fetchSite(auth = null)
        homeViewModel.fetchPosts(
            account = null,
            clear = true,
        )
    }
}
