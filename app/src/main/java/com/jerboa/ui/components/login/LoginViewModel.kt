package com.jerboa.ui.components.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.api.API
import com.jerboa.api.fetchPostsWrapper
import com.jerboa.api.getSiteWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.Login
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var jwt: String by mutableStateOf("")
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun login(
        instance: String,
        form: Login,
        navController: NavController,
        accountViewModel: AccountViewModel,
        siteViewModel: SiteViewModel,
        homeViewModel: HomeViewModel,
        ctx: Context,
    ) {
        val api = API.changeLemmyInstance(instance)

        viewModelScope.launch {
            try {
                loading = true
                try {
                    jwt = api.login(form = form).jwt
                } catch (e: java.net.UnknownHostException) {
                    loading = false
                    val msg = "$instance is not a Lemmy Instance"
                    Log.e("login", e.toString())
                    Toast.makeText(
                        ctx,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    this.cancel()
                    return@launch
                }
            } catch (e: Exception) {
                loading = false
                val msg = "Incorrect Login"
                Log.e("login", e.toString())
                Toast.makeText(
                    ctx,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
                this.cancel()
                return@launch
            }

            // Refetch the site to get your name and id
            // Can't do a co-routine within a co-routine
            siteViewModel.siteRes = getSiteWrapper(auth = jwt)

            val luv = siteViewModel.siteRes?.my_user!!.local_user_view
            val account = Account(
                id = luv.person.id,
                name = luv.person.name,
                current = true,
                instance = instance,
                jwt = jwt,
                defaultListingType = luv.local_user.default_listing_type,
                defaultSortType = luv.local_user.default_sort_type,
            )

            // Refetch the front page
            val posts = fetchPostsWrapper(
                account = account,
                ctx = ctx,
                listingType = ListingType.values()[
                    luv.local_user
                        .default_listing_type
                ],
                sortType = SortType.values()[
                    luv.local_user
                        .default_sort_type
                ],
                page = 1
            )
            homeViewModel.posts.clear()
            homeViewModel.posts.addAll(posts)

            // Remove the default account
            accountViewModel.removeCurrent()

            // Save that info in the DB
            accountViewModel.insert(account)

            loading = false

            navController.navigate(route = "home")
        }
    }
}
