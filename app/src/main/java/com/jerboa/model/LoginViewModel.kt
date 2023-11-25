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
import com.jerboa.api.toApiState
import com.jerboa.db.entity.Account
import com.jerboa.matchLoginErrorMsgToStringRes
import it.vercruysse.lemmyapi.LemmyApi
import it.vercruysse.lemmyapi.exception.NotSupportedException
import it.vercruysse.lemmyapi.v0x19.datatypes.Login
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
        viewModelScope.launch {
            loading = true
            try {
                val nodeInfo = LemmyApi.getNodeInfo(instance).getOrThrow()

                if (!LemmyApi.isLemmyInstance(nodeInfo)) {
                    throw Exception(ctx.getString(R.string.login_view_model_is_not_a_lemmy_instance, instance))
                }

                val api = API.createTempInstanceVersion(instance, LemmyApi.getVersion(nodeInfo))
                val resp = api.login(form = form).getOrThrow()
                api.auth = resp.jwt
                API.setLemmyInstance(api)
            } catch (e: NotSupportedException) {
                loading = false
                val msg = ctx.getString(R.string.server_version_not_supported, instance)
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                return@launch
            } catch (e: Exception) {
                loading = false
                val msg = matchLoginErrorMsgToStringRes(ctx, e)
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                return@launch
            }

            // Fetch the site to get your name and id
            siteViewModel.siteRes = API.getInstance().getSite().toApiState()

            try {
                when (val siteRes = siteViewModel.siteRes) {
                    is ApiState.Failure -> {
                        val txt = siteRes.msg.message ?: "FAILURE: NO MESSAGE, probably that version not supported"
                        Toast.makeText(ctx, txt, Toast.LENGTH_SHORT).show()
                        throw Exception(txt)
                    }

                    is ApiState.Success -> {
                        val luv = siteRes.data.my_user!!.local_user_view

                        if (accountViewModel.allAccounts.value?.any {
                                it.name.equals(
                                    luv.person.name,
                                    true,
                                ) && it.instance.equals(instance, true)
                            } == true
                        ) {
                            throw Exception(ctx.getString(R.string.login_already_logged_in))
                        }

                        val account =
                            Account(
                                id = luv.person.id,
                                name = luv.person.name,
                                current = true,
                                instance = instance,
                                jwt = API.getInstance().auth!!,
                                defaultListingType = luv.local_user.default_listing_type.ordinal,
                                defaultSortType = luv.local_user.default_sort_type.ordinal,
                                verificationState = 0,
                            )

                        // Remove the default account
                        accountViewModel.removeCurrent()
                        // Save that info in the DB
                        accountViewModel.insert(account)

                        loading = false
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
