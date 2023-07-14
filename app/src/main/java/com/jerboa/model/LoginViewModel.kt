package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.MINIMUM_API_VERSION
import com.jerboa.api.apiWrapper
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.compareVersions
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.Login
import com.jerboa.db.entity.Account
import com.jerboa.getHostFromInstanceString
import com.jerboa.serializeToMap
import com.jerboa.ui.components.common.toHome
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    fun login(
        instance: String,
        form: Login,
        navController: NavController,
        accountViewModel: AccountViewModel,
        siteViewModel: SiteViewModel,
        ctx: Context,
    ) {
        val originalInstance = API.currentInstance
        val api = API.changeLemmyInstance(getHostFromInstanceString(instance))
        var jwt: String

        viewModelScope.launch {
            try {
                loading = true
                try {
                    jwt = retrofitErrorHandler(api.login(form = form)).jwt!! // TODO this needs
                    // to be checked,
                } catch (e: java.net.UnknownHostException) {
                    loading = false
                    val msg = ctx.getString(
                        R.string.login_view_model_is_not_a_lemmy_instance,
                        instance,
                    )
                    Log.e("login", e.toString())
                    Toast.makeText(
                        ctx,
                        msg,
                        Toast.LENGTH_SHORT,
                    ).show()
                    API.changeLemmyInstance(originalInstance)
                    this.cancel()
                    return@launch
                }
            } catch (e: Exception) {
                loading = false
                val msg = ctx.getString(R.string.login_view_model_incorrect_login)
                Log.e("login", e.toString())
                Toast.makeText(
                    ctx,
                    msg,
                    Toast.LENGTH_SHORT,
                ).show()
                API.changeLemmyInstance(originalInstance)
                this.cancel()
                return@launch
            }

            // Fetch the site to get your name and id
            // Can't do a co-routine within a co-routine
            val getSiteForm = GetSite(auth = jwt)
            siteViewModel.siteRes = apiWrapper(API.getInstance().getSite(getSiteForm.serializeToMap()))

            when (val siteRes = siteViewModel.siteRes) {
                is ApiState.Failure -> {
                    Toast.makeText(
                        ctx,
                        siteRes.msg.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                is ApiState.Success -> {
                    val siteVersion = siteRes.data.version
                    if (compareVersions(siteVersion, MINIMUM_API_VERSION) < 0) {
                        val message = ctx.resources.getString(
                            R.string.dialogs_server_version_outdated_short,
                            siteVersion,
                        )
                        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                    }

                    try {
                        val luv = siteRes.data.my_user!!.local_user_view
                        val account = Account(
                            id = luv.person.id,
                            name = luv.person.name,
                            current = true,
                            instance = instance,
                            jwt = jwt,
                            defaultListingType = luv.local_user.default_listing_type.ordinal,
                            defaultSortType = luv.local_user.default_sort_type.ordinal,
                        )

                        // Remove the default account
                        accountViewModel.removeCurrent()

                        // Save that info in the DB
                        accountViewModel.insert(account)
                    } catch (e: Exception) {
                        loading = false
                        Log.e("login", e.toString())
                        API.changeLemmyInstance(originalInstance)
                        this.cancel()
                        return@launch
                    }

                    loading = false

                    navController.toHome()
                }

                else -> {}
            }
        }
    }
}
