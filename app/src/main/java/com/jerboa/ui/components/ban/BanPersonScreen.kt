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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.BanData
import com.jerboa.model.BanPersonViewModel
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.ActionTopBar
import it.vercruysse.lemmyapi.datatypes.MyUserInfo

object BanPersonReturn {
    const val BAN_DATA_VIEW = "ban-person::return(ban-data-view)"
    const val BAN_DATA_SEND = "ban-person::send(ban-data-view)"
}

@Composable
fun BanPersonScreen(
    appState: JerboaAppState,
    myUserInfo: MyUserInfo?,
) {
    Log.d("jerboa", "got to ban person screen")

    val ctx = LocalContext.current
    val resources = LocalResources.current

    val banPersonViewModel: BanPersonViewModel = viewModel()
    val banData = appState.getPrevReturn<BanData>(key = BanPersonReturn.BAN_DATA_SEND)

    var removeData by rememberSaveable { mutableStateOf(false) }
    var permaBan by rememberSaveable { mutableStateOf(false) }
    var expireDays: Long? by rememberSaveable { mutableStateOf(null) }

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }

    val loading =
        when (banPersonViewModel.banPersonRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    val title =
        stringResource(if (banData.banned) R.string.unban_person else R.string.ban_person, personNameShown(banData.person, true))

    val isBan = !banData.banned

    // Make sure the form is valid only if permaban is checked or expireDays is not null
    val isValid = !isBan or permaBan or (expireDays !== null)

    Scaffold(
        topBar = {
            ActionTopBar(
                formValid = isValid && !loading,
                title = title,
                loading = loading,
                onActionClick = {
                    if (myUserInfo != null) {
                        banPersonViewModel.banOrUnbanPerson(
                            personId = banData.person.id,
                            ban = isBan,
                            removeOrRestoreData = if (isBan) removeData else false,
                            expireDays = if (!isBan or permaBan) null else expireDays,
                            reason = reason.text,
                            ctx = ctx,
                            resources = resources,
                            focusManager = focusManager,
                        ) { personView ->
                            appState.apply {
                                addReturn(BanPersonReturn.BAN_DATA_VIEW, personView)
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
                myUserInfo = myUserInfo,
                padding = padding,
            )
        },
    )
}
