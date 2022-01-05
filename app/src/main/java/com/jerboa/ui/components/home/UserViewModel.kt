package com.jerboa.ui.components.home

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
import com.jerboa.datatypes.api.GetSite
import com.jerboa.datatypes.api.Login
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppDB
import com.jerboa.serializeToMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class UserViewModel : ViewModel() {

    var jwt: String by mutableStateOf("")
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun login(
        instance: String,
        form: Login,
        navController: NavController,
        accountViewModel: AccountViewModel,
        ctx: Context,
    ) {
        val api = API.setInstance(instance)

        viewModelScope.launch {
            try {
                loading = true
                try {
                    jwt = api.login(form = form).jwt
                } catch (e: java.net.UnknownHostException) {
                    val msg = "Could not find instance $instance"
                    Log.e("login", e.toString())
                    Toast.makeText(
                        ctx,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    exitProcess(0)
                }
            } catch (e: Exception) {
                Log.e("ViewModel: LoginViewModel", e.toString())
            } finally {
                loading = false

                // Fetch the site to get more info, such as your
                // name and avatar
                val siteForm = GetSite(jwt)
                val site = api.getSite(
                    siteForm
                        .serializeToMap()
                )
                val luv = site.my_user!!.local_user_view
                val account = Account(
                    id = luv.person.id,
                    selected = true,
                    instance = instance,
                    avatar = luv.person.avatar,
                    name = luv.person.name,
                    jwt = jwt,
                )

                // Save that info in the DB
                accountViewModel.insert(account)

                navController.navigate(route = "home")
            }
        }
    }
}
