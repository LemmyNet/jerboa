package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.feat.futureDaysToUnixTime
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.datatypes.BanPerson
import it.vercruysse.lemmyapi.datatypes.BanPersonResponse
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import kotlinx.coroutines.launch

class BanPersonViewModel : ViewModel() {
    var banPersonRes: ApiState<BanPersonResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun banOrUnbanPerson(
        personId: PersonId,
        ban: Boolean,
        removeData: Boolean? = null,
        reason: String,
        expireDays: Long? = null,
        ctx: Context,
        focusManager: FocusManager,
        onSuccess: (PersonView) -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                BanPerson(
                    person_id = personId,
                    ban = ban,
                    remove_data = removeData,
                    reason = reason,
                    expires = futureDaysToUnixTime(expireDays),
                )

            banPersonRes = ApiState.Loading
            banPersonRes = API.getInstance().banPerson(form).toApiState()

            when (val res = banPersonRes) {
                is ApiState.Failure -> {
                    Log.d("banPerson", "failed", res.msg)
                    apiErrorToast(msg = res.msg, ctx = ctx)
                }

                is ApiState.Success -> {
                    val personView = res.data.person_view
                    val personNameShown = personNameShown(personView.person, true)
                    val message =
                        if (ban) {
                            if (expireDays !== null) {
                                ctx.getString(R.string.person_banned_for_x_days, personNameShown, expireDays)
                            } else {
                                ctx.getString(R.string.person_banned, personNameShown)
                            }
                        } else {
                            ctx.getString(R.string.person_unbanned, personNameShown)
                        }
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()

                    focusManager.clearFocus()
                    onSuccess(personView)
                }
                else -> {}
            }
        }
    }
}
