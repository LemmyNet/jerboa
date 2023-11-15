package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.jerboa.matchLoginErrorMsgToStringRes
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var loading by mutableStateOf(false)
        private set

    fun login(
        instance: String,
        form: Login,
        onGoHome: () -> Unit,
        accountViewModel: AccountViewModel,
        siteViewModel: SiteViewModel,
        ctx: Context,
    ) {
        val newInstance = getHostFromInstanceString(instance)
        val api = API.createTempInstance(newInstance)
        var jwt: String

        viewModelScope.launch {
            loading = true
            try {
                jwt = retrofitErrorHandler(api.login(form = form)).jwt!!
            } catch (e: java.net.UnknownHostException) {
                loading = false
                val msg =
                    ctx.getString(
                        R.string.login_view_model_is_not_a_lemmy_instance,
                        instance,
                    )
                Log.d("login", msg, e)
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: Exception) {
                loading = false
                val msg = matchLoginErrorMsgToStringRes(ctx, e)
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                return@launch
            }

            // Fetch the site to get your name and id
            siteViewModel.siteRes = apiWrapper(api.getSite(GetSite(auth = jwt).serializeToMap()))

            try {
                when (val siteRes = siteViewModel.siteRes) {
                    is ApiState.Failure -> {
                        val txt = siteRes.msg.message ?: "FAILURE: NO MESSAGE, probably that version not supported"
                        Toast.makeText(ctx, txt, Toast.LENGTH_SHORT).show()
                        throw RuntimeException(txt)
                    }

                    is ApiState.Success -> {
                        val siteVersion = siteRes.data.version
                        if (compareVersions(siteVersion, MINIMUM_API_VERSION) < 0) {
                            val message =
                                ctx.resources.getString(
                                    R.string.dialogs_server_version_outdated_short,
                                    siteVersion,
                                )
                            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                        }

                        val luv = siteRes.data.my_user!!.local_user_view

                        if (accountViewModel.allAccounts.value?.any {
                                it.name.equals(
                                    luv.person.name,
                                    true,
                                ) && it.instance.equals(instance, true)
                            } == true
                        ) {
                            throw RuntimeException(ctx.getString(R.string.login_already_logged_in))
                        }

                        val account =
                            Account(
                                id = luv.person.id,
                                name = luv.person.name,
                                current = true,
                                instance = instance,
                                jwt = jwt,
                                defaultListingType = luv.local_user.default_listing_type.ordinal,
                                defaultSortType = luv.local_user.default_sort_type.ordinal,
                                verificationState = 0,
                            )

                        // Remove the default account
                        accountViewModel.removeCurrent()
                        // Save that info in the DB
                        accountViewModel.insert(account)

                        loading = false
                        API.changeLemmyInstance(newInstance)
                        onGoHome()
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                loading = false
                Log.e("login", "failed", e)
                Toast.makeText(ctx, e.message, Toast.LENGTH_SHORT).show()
                return@launch
            }
        }
    }
}
