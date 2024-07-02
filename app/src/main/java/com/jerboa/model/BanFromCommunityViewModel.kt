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
import com.jerboa.communityNameShown
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.feat.futureDaysToUnixTime
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.datatypes.BanFromCommunity
import it.vercruysse.lemmyapi.datatypes.BanFromCommunityResponse
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.PersonId
import kotlinx.coroutines.launch

class BanFromCommunityViewModel : ViewModel() {
    var banFromCommunityRes: ApiState<BanFromCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun banOrUnbanFromCommunity(
        personId: PersonId,
        community: Community,
        ban: Boolean,
        removeData: Boolean? = null,
        reason: String,
        expireDays: Long? = null,
        ctx: Context,
        focusManager: FocusManager,
        onSuccess: (BanFromCommunityData) -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                BanFromCommunity(
                    person_id = personId,
                    community_id = community.id,
                    ban = ban,
                    remove_data = removeData,
                    reason = reason,
                    expires = futureDaysToUnixTime(expireDays),
                )

            banFromCommunityRes = ApiState.Loading
            banFromCommunityRes = API.getInstance().banFromCommunity(form).toApiState()

            when (val res = banFromCommunityRes) {
                is ApiState.Failure -> {
                    Log.d("banFromCommunity", "failed", res.msg)
                    apiErrorToast(msg = res.msg, ctx = ctx)
                }

                is ApiState.Success -> {
                    val personNameShown = personNameShown(res.data.person_view.person, true)
                    val communityNameShown = communityNameShown(community)
                    val message =
                        if (ban) {
                            if (expireDays !== null) {
                                ctx.getString(
                                    R.string.person_banned_from_community_for_x_days,
                                    personNameShown,
                                    communityNameShown,
                                    expireDays,
                                )
                            } else {
                                ctx.getString(R.string.person_banned_from_community, personNameShown, communityNameShown)
                            }
                        } else {
                            ctx.getString(R.string.person_unbanned_from_community, personNameShown, communityNameShown)
                        }
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()

                    focusManager.clearFocus()
                    onSuccess(
                        BanFromCommunityData(
                            person = res.data.person_view.person,
                            community = community,
                            banned = res.data.banned,
                        ),
                    )
                }
                else -> {}
            }
        }
    }
}
