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
import com.jerboa.api.getSiteWrapper
import com.jerboa.datatypes.api.Login
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
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
            } finally {

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
                )

                // TODO
                // Refetch the front page
//                postListingsViewModel.fetchPosts(
//                    auth = jwt,
//                    clear = true,
//                )

                // Remove the default account
                accountViewModel.removeCurrent()

                // Save that info in the DB
                accountViewModel.insert(account)

                loading = false

                navController.navigate(route = "home")
            }
        }
    }
}
