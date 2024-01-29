package com.jerboa.ui.components.ban

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.communityNameShown
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.BanFromCommunityViewModel
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount

object BanFromCommunityReturn {
    const val BAN_DATA_VIEW = "ban-from-community::return(ban-data-view)"
    const val BAN_DATA_SEND = "ban-from-community::send(ban-data-view)"
}

@Composable
fun BanFromCommunityActivity(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to ban from community activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val banFromCommunityViewModel: BanFromCommunityViewModel = viewModel()
    val banData = appState.getPrevReturn<BanFromCommunityData>(key = BanFromCommunityReturn.BAN_DATA_SEND)
    val person = banData.person
    val community = banData.community
    val banned = banData.banned

    var removeData by rememberSaveable { mutableStateOf(false) }
    var permaBan by rememberSaveable { mutableStateOf(false) }
    var expireDays: Long? by rememberSaveable { mutableStateOf(null) }

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }

    val loading =
        when (banFromCommunityViewModel.banFromCommunityRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    val title =
        stringResource(
            if (banned) R.string.unban_person_from_community else R.string.ban_person_from_community,
            personNameShown(person, true),
            communityNameShown(community),
        )

    val isBan = !banned

    // Make sure the form is valid only if permaban is checked or expireDays is not null
    val isValid = !isBan or permaBan or (expireDays !== null)

    Scaffold(
        topBar = {
            ActionTopBar(
                formValid = isValid && !loading,
                title = title,
                loading = loading,
                onActionClick = {
                    if (!account.isAnon()) {
                        banFromCommunityViewModel.banOrUnbanFromCommunity(
                            personId = person.id,
                            community = community,
                            ban = isBan,
                            removeData = if (isBan) removeData else false,
                            expireDays = if (!isBan or permaBan) null else expireDays,
                            reason = reason.text,
                            ctx = ctx,
                            focusManager = focusManager,
                        ) { banData ->
                            appState.apply {
                                addReturn(BanFromCommunityReturn.BAN_DATA_VIEW, banData)
                                navigateUp()
                            }
                        }
                    }
                },
                actionText = R.string.form_submit,
                actionIcon = Icons.AutoMirrored.Outlined.Send,
                onBackClick = appState::popBackStack,
            )
        },
        content = { padding ->
            BanPersonBody(
                reason = reason,
                onReasonChange = { reason = it },
                isBan = isBan,
                expireDays = expireDays,
                onExpiresChange = {
                    expireDays = it
                    permaBan = false
                },
                permaBan = permaBan,
                onPermaBanChange = {
                    permaBan = it
                    expireDays = null
                },
                removeData = removeData,
                onRemoveDataChange = { removeData = it },
                isValid = isValid,
                account = account,
                padding = padding,
            )
        },
    )
}
